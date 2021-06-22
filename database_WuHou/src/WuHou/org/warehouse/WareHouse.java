package WuHou.org.warehouse;

import WuHou.org.attributes.ReWr;
import WuHou.org.flags.*;
import WuHou.org.util.*;

import javax.naming.Name;
import javax.xml.crypto.Data;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 总仓库
 * 就是一个存放了多个 DataBase 的包装袋
 */
public class WareHouse {
    /**
     * 用于记录数据库被添加的顺序
     */
    private final ArrayList<DataBase> wareHouseOrdered;

    /**
     * 用于记录数据库被添加的时间,
     * 同时允许通过指定时间查询对于的数据库。
     * get(2016) 查找所有 2016 年创建的数据库
     * get(2016/05) 查找所有 2016年 5月 创建的数据库
     * 使用 indexOf(String str)进行查询
     * <p>
     * 也用于历史检索，这个Map中所有的 db 都不会被删除，只会将在其它列表中被删除的数据库标记为 Lock
     */
    private final ArrayList<DataBase> wareHouseTime;

    /**
     * 用于键值引索
     */
    private final HashMap<String, DataBase> wareHouseEntry;

    /**
     * 数据库指针目前指向的 database
     */
    private int dBPointer; //数据库数量过多，或者循环次数过多的时候，可能出现隐患。

    /**
     * 时间反馈
     */
    public static SimpleDateFormat Fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    //Fmt.format(new Date())

    /**
     * 构造函数
     */
    public WareHouse() {
        this.wareHouseOrdered = new ArrayList<>();
        this.wareHouseEntry = new HashMap<>();
        this.wareHouseTime = new ArrayList<>();
        this.dBPointer = 0;
    }

    /**
     * 获取时间字符串
     */
    protected String getTime() {
        return String.format("%s", Fmt.format(new Date()));
    }

    /**
     * 获取 DBPointer
     */
    public int getDBPointer() {
        return dBPointer;
    }

    /**
     * 指针+1
     */
    public void updateDBPointer() {
        this.dBPointer++;
    }

    /**
     * 获取数据库的列表形式，包含了数据库被添加时的顺序，先创建的靠前
     *
     * @return 所有权限不为 LOCK 的数据库
     */
    public ArrayList<DataBase> getWareHouseOrdered() {
        ArrayList<DataBase> dBOrderList = new ArrayList<>();
        for (DataBase dataBases : this.wareHouseOrdered) {
            try {
                dBOrderList.add(permissionCheck(dataBases));
            } catch (PermissionDeniedException e) {
                //直接跳过
            }
        }
        return dBOrderList;
    }

    /**
     * 获取所有可编辑的数据库，顺序依照数据库创建的顺序，先创建的靠前
     *
     * @return 所有权限为 READ_WRITE 的数据库
     */
    public ArrayList<DataBase> getEditableDBList() {
        ArrayList<DataBase> dBOrderList = new ArrayList<>();
        for (DataBase dataBases : this.wareHouseOrdered) {
            if (dataBases.getPermission().equals(ReWr.READ_WRITE)) {
                dBOrderList.add(dataBases);
            }
        }
        return dBOrderList;
    }

    /**
     * 获取数据库的Map形式，只返回可可读写或者只读的 database。 跳过所有LOCK的 database。
     */
    public HashMap<String, DataBase> getWareHouseEntry() {
        HashMap<String, DataBase> dBEntry = new HashMap<>();
        for (Map.Entry<String, DataBase> entry : this.wareHouseEntry.entrySet()) {
            if (entry.getValue().getPermission().equals(ReWr.READ_WRITE) ||
                    entry.getValue().getPermission().equals(ReWr.READ_ONLY)) {
                dBEntry.put(entry.getKey(), entry.getValue());
            }
        }
        return dBEntry;
    }

    /**
     * 获取时间轴，时间轴的一切都是只读的。
     *
     * @return 所有时间轴中储存的 database 的只读复制体。
     */
    public ArrayList<DataBase> getWareHouseTime() {
        ArrayList<DataBase> dataBases = new ArrayList<>();
        for (DataBase dataBase : this.wareHouseTime) {
            dataBases.add(new DataBase(dataBase, ReWr.READ_ONLY));
        }
        return dataBases;
    }

    /**
     * 添加已有的 DataBase
     *
     * @param dataBase 数据库对象
     */
    public void addDataBase(DataBase dataBase) throws DataBaseDuplicateException {
        for (DataBase dataBases : this.wareHouseOrdered) {
            if (dataBases.getName().equals(dataBase.getName())) {
                throw new DataBaseDuplicateException(
                        "<ERROR> addDataBase: Duplicated DataBase Name");
            }
        }
        this.wareHouseOrdered.add(dataBase);
        this.wareHouseEntry.put(dataBase.getName(), dataBase);
        this.wareHouseTime.add(dataBase);
    }

    /**
     * 创建新的数据库对象并添加到 总仓库
     *
     * @param name 新建数据库的名字。
     */
    public DataBase newDataBase(String name) {
        try {
            DataBase dataBase = new DataBase(name, getTime());
            addDataBase(dataBase);
            return dataBase;
        } catch (DataBaseDuplicateException e) {
            System.err.println("<ERROR> Database creation failed");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 删除对应的database，只有RW可以删除
     *
     * @param name 待删除的数据库名字
     * @return where the database was in wareHouseOrdered
     */
    public int deleteDataBase(String name) throws PermissionDeniedException {
        try {
            if (getDatabase(name).getIfCopy() == 1) {
                throw new PermissionDeniedException(
                        "<ERROR> deleteDataBase: this database has been protected.");
            }
            int databasePos = this.wareHouseOrdered.indexOf(getDatabase(name));
            this.wareHouseOrdered.remove(getDatabase(name));
            //wareHouseTime必须放在wareHouseOrdered后面，因为它会改变权限。
            //因为wareHouseTime中添加的db和order里加的db都是一样的。所以只要这里对权限进行改变就可以了。
            getDatabase(name).setPermission(ReWr.LOCK);
            this.wareHouseEntry.remove(name);
            return databasePos;
        } catch (NoDataBaseExistException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 为数据库改名字，只有权限为允许 读写 的可以改名字
     * 调用此method会删除原来的 database，并创建一个除了名字以外一模一样的，再添加回 warehouse
     * 但时间轴中的 database不会被删除，而是其权限被设定为 LOCK，不再允许被读写。
     *
     * @param dBName  待改名的database
     * @param newName 新的名字
     */
    public void changeName(String dBName, String newName) {
        try {
            DataBase newDatabase = new DataBase(getDatabase(dBName), newName, getTime());
            //清理新db的ifCopy属性
            newDatabase.setIfCopy(0);
            //删除原有的database
            int dbPos = deleteDataBase(dBName);
            for (DataBase dataBases : this.wareHouseOrdered) {
                if (dataBases.getName().equals(newName)) {
                    throw new DataBaseDuplicateException(
                            "<ERROR> changeName: Duplicated DataBase Name");
                }
            }

            this.wareHouseOrdered.add(dbPos, newDatabase);
            this.wareHouseEntry.put(newDatabase.getName(), newDatabase);
            // 重命名后的 database 会被视作新的database，放置于时间轴末尾。
            this.wareHouseTime.add(newDatabase);
        } catch (PermissionDeniedException |
                DataBaseDuplicateException |
                NoDataBaseExistException e) {
            e.printStackTrace();
        }
    }

    /**
     * 权限过滤, 通过输入database来返回 database，本质目的只是单纯的检测该 database的权限。
     */
    private DataBase permissionCheck(DataBase dataBase) throws PermissionDeniedException {
        dataBase.setLastEditedTime(getTime());
        if (dataBase.getPermission() == ReWr.READ_WRITE) {
            return dataBase;
        } else if (dataBase.getPermission() == ReWr.READ_ONLY) {
            return new DataBase(dataBase);
        } else if (dataBase.getPermission() == ReWr.LOCK) {
            throw new PermissionDeniedException(
                    "<ERROR> permissionCheck: could not find database or maybe hidden, with name "
                            + dataBase.getName());
        } else {
            // 数据库的权限出现异常，因此默认为LOCK，并抛出异常
            this.wareHouseEntry.get(dataBase.getName()).setPermission(ReWr.LOCK);
            throw new PermissionDeniedException(
                    "<ERROR> permissionCheck: could not find database or maybe hidden, with name "
                            + dataBase.getName());
        }
    }

    /**
     * 提取 DataBase (通过键值)
     *
     * @param dataBaseName 待查找的数据库名字
     * @return DataBase if it is in
     * @throws NoDataBaseExistException if no DataBase found in wareHouse
     */
    public DataBase getDatabase(String dataBaseName) throws NoDataBaseExistException {
        if (this.wareHouseEntry.containsKey(dataBaseName)) {
            try {
                if (permissionCheck(this.wareHouseEntry.get(dataBaseName)).getIfCopy() == 1) {
                    System.err.println("<WARN> getDatabase: this is a read only database.");
                }
                return permissionCheck(this.wareHouseEntry.get(dataBaseName));
            } catch (PermissionDeniedException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            throw new NoDataBaseExistException(
                    "<ERROR> getDatabase: could not find database with name "
                            + dataBaseName);
        }
    }

    /**
     * 提取 DataBase (通过键值)
     *
     * @return DataBase 如果已经有该 DataBase，如果没有，则直接创建一个新的。
     */
    public DataBase getDatabaseAdv(String dataBaseName) {
        if (this.wareHouseEntry.containsKey(dataBaseName)) {
            try {
                if (permissionCheck(this.wareHouseEntry.get(dataBaseName)).getIfCopy() == 1) {
                    System.err.println("<WARN> getDatabaseAdv: this is a read only database.");
                }
                return permissionCheck(this.wareHouseEntry.get(dataBaseName));
            } catch (PermissionDeniedException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return newDataBase(dataBaseName);
        }
    }

    /**
     * 根据输入的名字从 总仓库 中搜索所有名字相近的 database
     * 只要数据库名字包含输入的字符串，或者数据库名字被输入的字符串包含，则被视为名字相近
     * 该方法不要求名字完全一致。
     *
     * @param name 用于搜索数据库的字符串
     * @return databaseList 所有名字中包含了输入的字符串，或者名字被输入的字符串包含的数据库。
     */
    public ArrayList<DataBase> searchDBByName(String name) {
        ArrayList<DataBase> simNameDB = new ArrayList<>();
        for (DataBase dataBase : this.wareHouseOrdered) {
            try {
                if (dataBase.getName().contains(name) || name.contains(dataBase.getName())) {
                    simNameDB.add(permissionCheck(dataBase));
                }
            } catch (PermissionDeniedException e) {
                // 自动跳过LOCK
            }
        }
        return simNameDB;
    }

    /**
     * 根据输入的名字从给定的 databaseList 中搜索所有名字相近的 database
     * 只要数据库名字包含输入的字符串，或者数据库名字被输入的字符串包含，则被视为名字相近
     * 该方法不要求名字完全一致。
     * (该方法方便用户迭代搜索)
     *
     * @param name 用于搜索数据库的字符串
     * @return databaseList 所有名字中包含了输入的字符串，或者名字被输入的字符串包含的数据库。
     */
    public ArrayList<DataBase> searchDBByName(String name, ArrayList<DataBase> databaseList) {
        ArrayList<DataBase> simNameDB = new ArrayList<>();
        for (DataBase dataBase : databaseList) {
            try {
                if (dataBase.getName().contains(name) || name.contains(dataBase.getName())) {
                    simNameDB.add(permissionCheck(dataBase));
                }
            } catch (PermissionDeniedException e) {
                // 自动跳过LOCK
            }
        }
        return simNameDB;
    }

    /**
     * 提取正在处理的 DataBase (通过创建顺序)
     * 用于遍历所有的 database, 且每次调用都只返回一个database, 再次调用指向下一个database。
     * (此方法不会输出权限为 LOCK的数据库)
     *
     * @return database 按照顺序返回 database，自动跳过所有LOCK的 database
     */
    public DataBase getDatabaseCycle() {
        int pointerIndex = getDBPointer() % this.wareHouseOrdered.size();
        updateDBPointer();
        try {
            if (permissionCheck(this.wareHouseOrdered.get(pointerIndex)).getIfCopy() == 1) {
                System.err.println("<WARN> getDatabaseCycle: this is a read only database.");
            }
            return permissionCheck(this.wareHouseOrdered.get(pointerIndex));
        } catch (PermissionDeniedException e) {
            // 如果之前那个因为权限无法输出，则自动进入下一个
            return getDatabaseCycle();
        }
    }

    /**
     * 通过创建日期从 总仓库 中搜索第一个符合输入时间的 database
     * 该方法要求输入的日期和待查找 database完全一致
     * (此方法不会输出权限为 LOCK的数据库)
     *
     * @return the First database which fit the input create time
     * @throws NoDataBaseExistException if no DataBase found in wareHouse
     */
    public DataBase searchDBByTime(String time) throws NoDataBaseExistException {
        for (DataBase dataBase : this.wareHouseTime) {
            if (dataBase.getTimeCreated().equals(time)) {
                try {
                    if (permissionCheck(dataBase).getIfCopy() == 1) {
                        System.err.println(
                                "<WARN> searchDBByTime: this is a read only database.");
                    }
                    return permissionCheck(dataBase);
                } catch (PermissionDeniedException e) {
                    //continue
                    //如果是权限问题导致无法查找则跳过。
                }
            }
        }
        // TODO 这里我应该 return null 还是抛出异常？
        throw new NoDataBaseExistException(
                "<ERROR searchDBByTime: No dataBase created at that time>");
    }

    /**
     * 通过创建日期从给定的 databaseList 中搜索第一个符合输入时间的 database
     * 该方法要求输入的日期和待查找 database完全一致
     * (此方法不会输出权限为 LOCK的数据库)
     * (此方法方便用户迭代搜索)
     *
     * @return the First database which fit the input create time
     * @throws NoDataBaseExistException if no DataBase found in wareHouse
     */
    public DataBase searchDBByTime(
            String time,
            ArrayList<DataBase> dataBasesList) throws
            NoDataBaseExistException {
        for (DataBase dataBase : dataBasesList) {
            if (dataBase.getTimeCreated().equals(time)) {
                try {
                    if (permissionCheck(dataBase).getIfCopy() == 1) {
                        System.err.println(
                                "<WARN> searchDBByTime: this is a read only database.");
                    }
                    return permissionCheck(dataBase);
                } catch (PermissionDeniedException e) {
                    //continue
                    //如果是权限问题导致无法查找则跳过。
                }
            }
        }
        // TODO 这里我应该 return null 还是抛出异常？
        throw new NoDataBaseExistException(
                "<ERROR searchDBByTime: No dataBase created at that time>");
    }

    /**
     * 通过创建日期从 总仓库 中搜索第一个符合输入时间的 database
     * 该方法 不 要求输入的日期彻底符合
     * (此方法不会输出权限为 LOCK的数据库)
     *
     * @return the First database which fit the input create time
     * @throws NoDataBaseExistException if no DataBase found in wareHouse
     */
    public DataBase searchDBByTimeUnclear(String time)
            throws NoDataBaseExistException {
        for (DataBase dataBase : this.wareHouseTime) {
            if (dataBase.getTimeCreated().contains(time)) {
                try {
                    if (permissionCheck(dataBase).getIfCopy() == 1) {
                        System.err.println(
                                "<WARN> searchDBByTimeUnclear: this is a read only database.");
                    }
                    return permissionCheck(dataBase);
                } catch (PermissionDeniedException e) {
                    //如果是权限问题导致无法查找则跳过。
                }
            }
        }
        // TODO 这里我应该 return null 还是抛出异常？
        throw new NoDataBaseExistException(
                "<ERROR> searchDBByTimeUnclear: No dataBase created at that time>");
    }

    /**
     * 通过创建日期从给定的 databaseList 中搜索第一个符合输入时间的 database
     * 该方法 不 要求输入的日期彻底符合
     * (此方法不会输出权限为 LOCK的数据库)
     *
     * @return the First database which fit the input create time
     * @throws NoDataBaseExistException if no DataBase found in wareHouse
     */
    public DataBase searchDBByTimeUnclear(
            String time, ArrayList<DataBase> databaseList)
            throws NoDataBaseExistException {
        for (DataBase dataBase : databaseList) {
            if (dataBase.getTimeCreated().contains(time)) {
                try {
                    if (permissionCheck(dataBase).getIfCopy() == 1) {
                        System.err.println(
                                "<WARN> searchDBByTimeUnclear: this is a read only database.");
                    }
                    return permissionCheck(dataBase);
                } catch (PermissionDeniedException e) {
                    //如果是权限问题导致无法查找则跳过。
                }
            }
        }
        // TODO 这里我应该 return null 还是抛出异常？
        throw new NoDataBaseExistException(
                "<ERROR> searchDBByTimeUnclear: No dataBase created at that time>");
    }

    /**
     * 从 总仓库 中搜索所有符合输入时间段的 DataBase (通过创建日期)
     * 该方法 不 要求输入的日期彻底符合
     * (此方法不会输出权限为 LOCK的数据库)
     * (模糊搜索，即只要时间 key中有符合的字符串段，就输出。)
     * 此方法返回的数据库列表中，Read_Only 和 Read_Write 权限的数据库会混杂在一起，
     * 不推荐通过此方法调用并修改数据库。
     *
     * @param time 待搜索的时间段
     * @return dBInDate 包含所有符合时间段内的 database (被上锁的除外)
     */
    public ArrayList<DataBase> searchDBByTimeRange(String time) {
        ArrayList<DataBase> dBInDate = new ArrayList<>();
        for (DataBase dataBase : this.wareHouseTime) {
            if (dataBase.getTimeCreated().contains(time)) {
                try {
                    dBInDate.add(permissionCheck(dataBase));
                } catch (PermissionDeniedException e) {
                    System.out.println("<INFO> searchDBByTimeRange: " +
                            "Jump through a dataBase which has been locked.");
                    //continue;
                }
            }
        }
        return dBInDate;
    }

    /**
     * 从给定的 databaseList 中搜索所有符合输入时间段的 DataBase (通过创建日期)
     * (模糊搜索，即只要键值 key中有符合的字符段，就输出。
     * 例如键值为 2002/12/08 12:10:02, 输入 12/08, 有符合字符段，输出。)
     * (此方法不会输出权限为 LOCK的数据库)
     * 此方法返回的数据库列表中，Read_Only 和 Read_Write 权限的数据库会混杂在一起，
     * 不推荐通过此方法调用并修改数据库。
     * 此方法方便用户迭代搜索。
     *
     * @param time 待搜索的时间段
     * @return dBInDate 包含所有符合时间段内的 database (被上锁的除外)
     */
    public ArrayList<DataBase> searchDBByTimeRange(
            String time, ArrayList<DataBase> databaseList) {

        ArrayList<DataBase> dBFitDate = new ArrayList<>();
        for (DataBase dataBase : databaseList) {
            if (dataBase.getTimeCreated().contains(time)) {
                try {
                    dBFitDate.add(permissionCheck(dataBase));
                } catch (PermissionDeniedException e) {
                    System.out.println("<INFO> searchDBByTimeRange: " +
                            "Jump through a dataBase which has been locked.");
                    //continue;
                }
            }
        }
        return dBFitDate;
    }

    /**
     * 通过属性标签从 总仓库 中搜索相似的 database
     * (此方法不会输出权限为 LOCK的数据库)
     * 此方法返回的数据库列表中，Read_Only 和 Read_Write 权限的数据库会混杂在一起，
     * 不推荐通过此方法调用并修改数据库。
     *
     * @param att attribute of database
     * @return a list of database that contain the attribute which has been in put.
     */
    public ArrayList<DataBase> searchDBByAttribute(String att) {
        ArrayList<DataBase> sameAttDB = new ArrayList<>();
        for (DataBase dataBase : this.wareHouseOrdered) {
            if (dataBase.getAttribute().contains(att)) {
                try {
                    sameAttDB.add(permissionCheck(dataBase));
                } catch (PermissionDeniedException e) {
                    //继续
                    System.out.println("<INFO> searchDBByAttribute: " +
                            "Jumped through a database which has been locked.");
                }
            }
        }
        return sameAttDB;
    }

    /**
     * 通过属性标签从 给定的 database列表 中搜索相似的 database
     * (此方法不会输出权限为 LOCK的数据库)
     * (此方法方便用户迭代搜素)
     *
     * @param att attribute of database
     * @return a list of database that contain the attribute which has been in put.
     */
    public ArrayList<DataBase> searchDBByAttribute(
            String att, ArrayList<DataBase> databaseList) {
        ArrayList<DataBase> sameAttDB = new ArrayList<>();
        for (DataBase dataBase : databaseList) {
            if (dataBase.getAttribute().contains(att)) {
                try {
                    sameAttDB.add(permissionCheck(dataBase));
                } catch (PermissionDeniedException e) {
                    //继续
                    System.out.println("<INFO> searchDBByAttribute: " +
                            "Jumped through a database which has been locked.");
                }
            }
        }
        return sameAttDB;
    }

    /**
     * 数据库搜索引擎
     * 调用所有search，查询所有可能和 key有关的 database
     *
     * @param key 待搜索的关键字
     * @return 包含所有可能和关键字key有关的 database 的 databaseList
     */
    public ArrayList<DataBase> searchDBs(String key) {
        ArrayList<DataBase> databaseList = new ArrayList<>();
        try {
            // 名称搜索
            for (DataBase dataBase : searchDBByName(key)) {
                if (!databaseList.contains(dataBase)) {
                    // 此处所有的数据库都已经经过permissionCheck了
                    databaseList.add(dataBase);
                }
            }
            // 时间搜素
            for (DataBase dataBase : searchDBByTimeRange(key)) {
                if (!databaseList.contains(dataBase)) {
                    databaseList.add(dataBase);
                }
            }
            // 关键字搜索
            for (DataBase dataBase : searchDBByAttribute(key)) {
                if (!databaseList.contains(dataBase)) {
                    databaseList.add(dataBase);
                }
            }
        } catch (Exception e) {
            // 暂时无视
        }
        return databaseList;
    }

    /**
     * 数据库搜索引擎
     * 调用所有search，查询所有可能和 key有关的 database
     * (此方法会将通过每个搜索方法获得的 databaseList 归类放到 对应的 Map键值下
     * Time : searchByTime
     * Name : searchByName
     * Attr : searchByAttr)
     *
     * @param key 带搜索的关键字
     * @return 包含所有可能和关键字key有关的 database 的 databaseMap
     */
    public HashMap<String, ArrayList<DataBase>> searchDBsMap(String key) {
        HashMap<String, ArrayList<DataBase>> databaseMap = new HashMap<>();
        try {
            // 名称搜索
            databaseMap.put("Name", searchDBByName(key));
            // 时间搜素
            databaseMap.put("Time", searchDBByTimeRange(key));
            // 关键字搜索
            databaseMap.put("Attr", searchDBByAttribute(key));
        } catch (Exception e) {
            // 暂时无视
        }
        return databaseMap;
    }

    /**
     * 补位函数
     * 用于检测参数字符串的长度，并作调整，再打印输出。
     *
     * @param content 输入的字符串
     * @return String 调整长度后的字符串。
     */
    public String lengthAlert(String content) {
        if (content.length() >= 8) {
            // 0123456789
            // 0123456_|
            return String.format("%s_", content.substring(0, 7));
        } else {
            // 0123
            // 0123____|
            return content + " ".repeat(8 - content.length());
        }
    }

    /**
     * 检测数据库数据是否正常
     * 有无出现数据量不对等的情况。
     * TODO
     */
    public boolean verify(String DataBaseName) {
        return true;
    }

    /**
     * 重写 toString() 方法
     * TODO  该方法中没有调用verify，也没有检测权限。纯粹是我懒了。
     */
    @Override
    public String toString() {
        StringBuilder wareHouseS = new StringBuilder();
        wareHouseS.append("Now print all DataBase\n");
        // 遍历所有dataBase
        for (Map.Entry<String, DataBase> entry : this.wareHouseEntry.entrySet()) {
            if (!verify(entry.getKey())) {
                wareHouseS.append("==============================================\n");
                wareHouseS.append(String.format("<Error> Database [%s] has some problem.\n",
                        entry.getKey()));
            }
            // 间隔符
            wareHouseS.append("==============================================\n");
            // dataBase 名字和数据库描述
            wareHouseS.append(String.format("[%s] \nRef: %s\n", entry.getKey(), entry.getValue().getReference()));
            // dataBase 读写权限
            wareHouseS.append(String.format("Permission: /%s/\n", entry.getValue().getPermission().name()));
            // 创建时间
            wareHouseS.append((String.format("Created on:        |%s|\n", entry.getValue().getTimeCreated())));
            // 最后查看时间
            wareHouseS.append((String.format("Last checked time: |%s|\n", entry.getValue().getTimeCreated())));
            // 遍历所有 key 作为表格第一行
            wareHouseS.append("________| ");
            for (Key keys : entry.getValue().getKeys()) {
                wareHouseS.append(String.format("%s ", lengthAlert(keys.getName())));
            }
            wareHouseS.append("\n");
            // 遍历Tar的长度（这里Tar的长度应该和dataBase.的外层长度相同。
            for (int i = 0; i < entry.getValue().getTargets().size(); i++) {
                wareHouseS.append(lengthAlert(entry.getValue().getTargets().get(i).getName()));
                wareHouseS.append("| ");
                for (String value : entry.getValue().getValuesList().get(i)) {
                    wareHouseS.append(String.format("%s ", lengthAlert(value)));
                }
                wareHouseS.append("\n");
            }
        }
        return wareHouseS.toString();
    }

    /**
     * 打印时间轴
     */
    public void printTimeLine() {
        StringBuilder wareHouseT = new StringBuilder();
        wareHouseT.append("开始打印时间轴\n");
        for (DataBase dataBase : this.wareHouseTime) {
            wareHouseT.append(dataBase.getTimeCreated()).append(" : ");
            wareHouseT.append(dataBase.getName()).append(" : ");
            wareHouseT.append(dataBase.getPermission()).append("\n");
        }
        System.out.println(wareHouseT.toString());
    }

    public static void main(String[] args) {
        WareHouse wh = new WareHouse();
        wh.newDataBase("WuHou");
        //wh.newDataBase("Carole");
        try {
            wh.getDatabase("WuHou").addTar("MyCarole");
            wh.getDatabase("WuHou").addKey("MyCarole");
            wh.getDatabase("WuHou").addValue("Yeah!!!", "MyCarole", "MyCarole");
            wh.getDatabase("WuHou").addNamedKeys(5, "CarolK");
            wh.getDatabase("WuHou").addNamedTars(5, "CarolT");
            wh.getDatabase("WuHou").setReference("My Carole!~~");
            wh.getDatabase("WuHou").memsetValueRange("0", 1, 4, 1, 4);
            wh.getDatabase("WuHou").memsetValueRange("♡", 2, 3, 2, 3);
            wh.getDatabase("WuHou").memsetValueRange("*", "CarolT4", "CarolT4", "CarolK0", "CarolK3");

            wh.getDatabaseAdv("Carole").formNamedValued(6, 6, "[☭]", "Ling", "Ling");

            wh.changeName("WuHou", "Linger");

            System.out.println(wh.toString());
            wh.getDatabaseAdv("Carole2").formNamedValued(2, 6, "[0]", "Ling", "Ling");
            System.out.println(wh.searchDBs("Car"));
            wh.printTimeLine();
        } catch (NoDataBaseExistException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            System.err.println("发生了什么？");
            e2.printStackTrace();
        }

    }

    //public static void main(String[] args) throws
    //        NoExistTargetException, NoExistKeyException, FlagDuplicateException {
    //    WareHouse wh = new WareHouse();
    //    wh.getDatabaseAdv("Carole").formNamedValued(6, 6, "[☭]", "Ling", "Ling");
    //    System.out.println(wh.toString());
    //}


}
