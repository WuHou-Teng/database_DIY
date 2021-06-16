package WuHou.org.warehouse;

import WuHou.org.attributes.ReWr;
import WuHou.org.flags.*;
import WuHou.org.util.DataBaseDuplicateException;
import WuHou.org.util.NoDataBaseExistException;

import javax.xml.crypto.Data;
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
     */
    private final HashMap<String, DataBase> wareHouseTime;

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
    public static SimpleDateFormat Fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:ms");
    //Fmt.format(new Date())

    /**
     * 构造函数
     */
    public WareHouse() {
        this.wareHouseOrdered = new ArrayList<>();
        this.wareHouseEntry = new HashMap<>();
        this.wareHouseTime = new HashMap<>();
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
        this.wareHouseTime.put(dataBase.getTimeCreated(), dataBase);
    }

    /**
     * 获取数据库的列表形式，包含了数据库被添加时的顺序。
     */
    public ArrayList<DataBase> getWareHouseOrdered() {
        return wareHouseOrdered;
    }

    /**
     * 获取数据库的Map形式
     */
    public HashMap<String, DataBase> getWareHouseEntry() {
        return wareHouseEntry;
    }

    /**
     * 获取包含数据库添加日期的 Map
     */
    public HashMap<String, DataBase> getWareHouseTime() {
        return wareHouseTime;
    }

    /**
     * 创建新的数据库对象并添加到 总仓库
     *
     * @param name 新建数据库的名字。
     */
    public void newDataBase(String name) {
        try {
            DataBase dataBase = new DataBase(name, getTime());
            addDataBase(dataBase);
        } catch (DataBaseDuplicateException e) {
            System.err.println("<ERROR> Database creation failed");
            e.printStackTrace();
        }
    }

    /**
     * 权限过滤, 通过输入database来返回 database，本质目的只是单纯的检测该 database的权限。
     */
    private DataBase permissionCheck(DataBase dataBase) throws NoDataBaseExistException {
        dataBase.setLastEditedTime(getTime());
        if (dataBase.getPermission() == ReWr.READ_WRITE) {
            return dataBase;
        } else if (dataBase.getPermission() == ReWr.READ_ONLY) {
            return new DataBase(dataBase);
        } else if (dataBase.getPermission() == ReWr.LOCK) {
            throw new NoDataBaseExistException(
                    "<ERROR> getDatabase: could not find database or maybe hidden, with name "
                            + dataBase.getName());
        } else {
            // 数据库的权限出现异常，因此默认为LOCK，并抛出异常
            this.wareHouseEntry.get(dataBase.getName()).setPermission(ReWr.LOCK);
            throw new NoDataBaseExistException(
                    "<ERROR> getDatabase: could not find database or maybe hidden, with name "
                            + dataBase.getName());
        }
    }

    /**
     * 提取 DataBase (通过键值)
     *
     * @return DataBase if it is in
     * @throws NoDataBaseExistException if no DataBase found in wareHouse
     */
    public DataBase getDatabase(String dataBaseName) throws NoDataBaseExistException {
        if (this.wareHouseEntry.containsKey(dataBaseName)) {
            try {
                return permissionCheck(this.wareHouseEntry.get(dataBaseName));
            } catch (NoDataBaseExistException e) {
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
     * 提取正在处理的 DataBase (通过创建顺序)
     * 用于遍历所有的 database, 且每次调用都只返回一个database, 再次调用指向下一个database。
     */
    public DataBase getDatabaseCycle() {
        int pointerIndex = getDBPointer() % getWareHouseOrdered().size();
        updateDBPointer();
        try {
            return permissionCheck(this.getWareHouseOrdered().get(pointerIndex));
        } catch (NoDataBaseExistException e) {
            // 如果之前那个因为权限无法输出，则自动进入下一个
            return getDatabaseCycle();
        }
    }

    /**
     * 提取 DataBase (通过创建日期)
     * TODO 以后或许会添加不完全时间输入，搜索所有符合时间段的 DataBase
     *
     * @return DataBase if it is in
     * @throws NoDataBaseExistException if no DataBase found in wareHouse
     */
    public DataBase getDatabaseByTime(String time) throws NoDataBaseExistException {
        if (this.wareHouseTime.containsKey(time)) {
            try {
                return permissionCheck(this.wareHouseTime.get(time));
            } catch (NoDataBaseExistException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            throw new NoDataBaseExistException("<ERROR getDatabaseByDate: No dataBase created at that time>");
        }
    }

    /**
     * 提取 DataBase (通过创建日期)(模糊搜索，即只要时间 key中有符合的字符串段，就输出。)
     *
     * @param date 待搜索的时间段
     * @return dBInDate 包含所有符合时间段内的 database (被上锁的除外)
     */
    public ArrayList<DataBase> getDatabaseByDateRange(String date) {
        ArrayList<DataBase> dBInDate = new ArrayList<>();
        for (Map.Entry<String, DataBase> entry : this.wareHouseTime.entrySet()) {
            if (entry.getKey().contains(date)) {
                try {
                    dBInDate.add(permissionCheck(entry.getValue()));
                } catch (NoDataBaseExistException e) {
                    System.out.println("Jump through a dataBase which has been locked.");
                    //continue;
                }
            }
        }
        return dBInDate;
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

    public static void main(String[] args) throws NoDataBaseExistException {
        WareHouse wh = new WareHouse();
        wh.newDataBase("WuHou");
        wh.newDataBase("Carole");
        try {
            wh.getDatabase("WuHou").addTar("MyCarole");
            wh.getDatabase("WuHou").addKey("MyCarole");
            wh.getDatabase("WuHou").addValue("Yeah!!!", "MyCarole", "MyCarole");
            wh.getDatabase("WuHou").addNamedKeys(5,"CarolK");
            wh.getDatabase("WuHou").addNamedTars(5,"CarolT");
            wh.getDatabase("WuHou").setReference("My Carole!~~");
            wh.getDatabase("WuHou").memsetValueRange("0", 1, 4, 1,4);
            wh.getDatabase("WuHou").memsetValueRange("♡", 2, 3, 2,3);
            wh.getDatabase("WuHou").memsetValueRange("*", "CarolT4","CarolT4","CarolK0","CarolK3");

            //wh.getDatabase("Carole").addNamedKeys(5, "LingK");
            //wh.getDatabase("Carole").addNamedTars(5, "LingT");
            wh.getDatabase("Carole").formNamedValued(6, 6, "[☭]", "Ling", "Ling");
        } catch (NoDataBaseExistException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            System.err.println("发生了什么？");
            e2.printStackTrace();
        }
        System.out.println(wh.toString());
    }
}
