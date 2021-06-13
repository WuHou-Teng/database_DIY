package WuHou.org.warehouse;

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
    /** 用于记录数据库被添加的顺序 */
    private final ArrayList<DataBase> wareHouseOrdered;

    /**
     * 用于记录数据库被添加的时间,
     * 同时允许通过指定时间查询对于的数据库。
     * get(2016) 查找所有 2016 年创建的数据库
     * get(2016/05) 查找所有 2016年 5月 创建的数据库
     * 使用 indexOf(String str)进行查询
     */
    private final HashMap<String, DataBase> wareHouseTime;

    /** 用于键值引索 */
    private final HashMap<String, DataBase> wareHouseEntry;

    /** 时间反馈 */
    static SimpleDateFormat Fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:ms");
    //Fmt.format(new Date())

    /**
     * 构造函数
     */
    public WareHouse() {
        this.wareHouseOrdered = new ArrayList<>();
        this.wareHouseEntry = new HashMap<>();
        this.wareHouseTime = new HashMap<>();
    }

    /**
     * 获取时间字符串
     */
    protected String getTime() {
        return String.format("|%s|", Fmt.format(new Date()));
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
     * 提取 DataBase (通过键值)
     * @return DataBase if it is in
     * @throws NoDataBaseExistException if no DataBase found in wareHouse
     */
    public DataBase getDatabase(String dataBaseName) throws NoDataBaseExistException {
        if (this.wareHouseEntry.containsKey(dataBaseName)) {
            return this.wareHouseEntry.get(dataBaseName);
        } else {
            throw new NoDataBaseExistException("<ERROR> getDatabase: could not find database");
        }
    }

    /**
     * 提取 DataBase (通过创建顺序)
     */


    /**
     * 提取 DataBase (通过创建日期)
     * TODO 以后或许会添加不完全时间输入，搜索所有符合时间段的 DataBase
     *
     * @return DataBase if it is in
     * @throws NoDataBaseExistException if no DataBase found in wareHouse
     */
    public DataBase getDatabaseByDate(String time) throws NoDataBaseExistException {
        if (this.wareHouseTime.containsKey(time)) {
            return this.wareHouseTime.get(time);
        } else {
            throw new NoDataBaseExistException("<ERROR getDatabaseByDate: No dataBase created at that time>");
        }
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
            StringBuilder text = new StringBuilder();
            // 0123
            // 0123____|
            text.append(content);
            text.append(" ".repeat(8 - content.length()));
            return text.toString();
        }
    }

    /**
     * 检测数据库数据是否正常
     * 有无出现数据量不对等的情况。
     * TODO
     */
    public boolean verify (String DataBaseName) {
        return true;
    }

    /**
     * 重写 toString() 方法
     * TODO  添加 Try block
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
                //wareHouseS.append("==============================================\n");
            }
            // 间隔符
            wareHouseS.append("==============================================\n");
            // dataBase 名字和创建时间
            wareHouseS.append(String.format("[%s] Data: %s\n", entry.getKey(),
                    entry.getValue().getTimeCreated()));
            // 遍历所有 key 作为表格第一行
            wareHouseS.append("________| ");
            for (Key keys : entry.getValue().getKeys()){
                wareHouseS.append(String.format("%s ", lengthAlert(keys.getName())));
            }
            wareHouseS.append("\n");
            // 遍历Tar的长度（这里Tar的长度应该和dataBase.的外层长度相同。
            for (int i = 0; i < entry.getValue().getTargets().size(); i ++) {
                wareHouseS.append(lengthAlert(entry.getValue().getTargets().get(i).getName()));
                wareHouseS.append("| ");
                for (String value : entry.getValue().getValues().get(i)) {
                    wareHouseS.append(String.format("%s ", lengthAlert(value)));
                }
                wareHouseS.append("\n");
            }
        }
        return wareHouseS.toString();
    }
}
