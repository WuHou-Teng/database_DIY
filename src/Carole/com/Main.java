package Carole.com;

import java.text.SimpleDateFormat;
import java.util.*;


public class Main {
    /**
     * 总库
     * 其下包含多个数据库
     */
    private static HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> DB = new HashMap<>();

    private static final String READ_WRITE = "RW";
    private static final String READ_ONLY = "RO";
    private static final String LOCK = "LO";

    //private static final String KeyWords = "Carol";

    /**
     * 时间反馈
     */
    static SimpleDateFormat Fmt = new SimpleDateFormat("yy/MM/dd HH:mm:ss:ms");

    /**
     * 获取时间字符串
     */
    protected static String getTime() {
        return String.format("|%s|", Fmt.format(new Date()));
    }


    /**
     * 用于创建一个新的数据库并放到上面的总库中。
     *
     * @param Name 新数据库的名字
     * @param Key  新数据库中的键值
     */
    public static void BuildDB(String Name, ArrayList<String> Key) {//数据库初始化(建库)
        try {
            // 如果名字不为空
            if (!Name.equals("")) {
                // 新创建一个HashMap，FS，也就是新的数据库
                HashMap<String, HashMap<String, ArrayList<String>>> FS = new HashMap<>();//单个库（目录）
                HashMap<String, ArrayList<String>> SS = new HashMap<>();// SS 作为value的记录Map
                HashMap<String, ArrayList<String>> K = new HashMap<>();// K 作为Key的记录Map
                HashMap<String, ArrayList<String>> Nm = new HashMap<>(); // Nm作为Tar的记录Map

                HashMap<String, ArrayList<String>> Att = new HashMap<>();// 总属性
                ArrayList<String> Attt = new ArrayList<>();
                Attt.add(READ_WRITE); //默认权限是可读可写
                Att.put("Att", Attt);

                ArrayList<String> T = new ArrayList<>(); // 新建ArrayList，用于记录Tar，这个只是用来打包Tar
                ArrayList<String> Ref = new ArrayList<>();
                Ref.add(""); // 将列表KeyList 放到 Keymap 中
                K.put("Key", Key); // 添加reference 列表
                K.put("Ref", Ref); // 将打包的Tar列表放入TarMap中 作为TarMap
                K.put("Att", Attt);

                Nm.put("Tar", T); // 将 TarMap 放到FSMap中
                Nm.put("Att", Attt);

                FS.put("Tar", Nm); // 将 KeyMap 放入FSMap中
                FS.put("Key", K); // 将 valueMap 放入FSMap中
                FS.put("Value", SS); // 将FSMap 放入总数据库中。
                FS.put("Att", Att);
                DB.put(Name, FS);
                System.out.println(getTime() +
                        "<INFO>DBbuilder:Successfully built DB <" + Name + ">");
            } else {
                System.err.println("<ERROR>DBbulider:Empty DB name!");
            }
        } catch (Exception e) {
            System.err.println("<ERROR>DBbulider:DB initialization failed!");
            e.printStackTrace();
        }
    }

    /**
     * 获得 AttributeMap
     *
     * @param name database name
     * @return AttrMap
     */
    private static HashMap<String, ArrayList<String>> getAttrMap(String name) throws
            EmptyDataBaseNameException,
            NoDataBaseExistException,
            AttributeException {
        if (name.equals("")) {
            throw new EmptyDataBaseNameException();
        }
        if (DB.containsKey(name) && DB.get(name) != null) {
            try {
                return DB.get(name).get("Att");
            } catch (NullPointerException e) {
                HashMap<String, ArrayList<String>> Att = new HashMap<>();// 总属性
                ArrayList<String> Attt = new ArrayList<>();
                Attt.add(LOCK); //如果一个database的权限是缺失的，则默认视为LOCK
                Att.put("Att", Attt);
                DB.get(name).put("Att", Att);
                throw new AttributeException("<ERROR> getAttrMap:Attribute Missed, has been set to LOCK");
            }
        } else {
            System.err.println(getTime() + "<ERROR> getAttrMap:Non-existent Database <" + name + ">");
            throw new NoDataBaseExistException(name);
        }
    }

    /**
     * 获得 AttributeList
     *
     * @param name 数据库名字
     */
    private static ArrayList<String> getAttrList(String name) throws
            EmptyDataBaseNameException,
            NoDataBaseExistException,
            AttributeException {
        try {
            return getAttrMap(name).get("Att");
        } catch (NullPointerException e) {
            ArrayList<String> Attt = new ArrayList<>();
            Attt.add(LOCK); //如果一个database的权限是缺失的，则默认视为LOCK
            DB.get(name).get("Att").put("Att", Attt);
            throw new AttributeException("<ERROR> getAttrList:Attribute Missed, has been seen as LOCK");
        }
    }

    /**
     * 获得 dataBaseMap
     *
     * @return Database 返回存放了一个 database 的 Map
     */
    public static HashMap<String, HashMap<String, ArrayList<String>>> getDataBase(String name) throws
            NoDataBaseExistException,
            EmptyDataBaseNameException,
            AttributeException {
        if (name.equals("")) {
            throw new EmptyDataBaseNameException();
        }
        if (DB.containsKey(name) && DB.get(name) != null) {
            String attr = getAttrList(name).get(0);
            if (attr.equals(LOCK)) {
                throw new AttributeException("<ERROR> getDataBase:Access denied");
            } else if (attr.equals(READ_ONLY)) {
                return new HashMap<>(DB.get(name)); //返回一个只读的 databaseMap
            } else if (attr.equals(READ_WRITE)) {
                return DB.get(name); //返回 databaseMap 原文件
            } else {
                throw new AttributeException("<ERROR> getDataBase:UNKNOWN Attribute");
            }
        } else {
            System.err.println(getTime() + "<ERROR> getValue:Non-existent Database <" + name + ">");
            throw new NoDataBaseExistException(name);
        }
    }

    /**
     * 修改数据库的总属性
     *
     * @param name      数据库名称
     * @param attrOrder 希望修改的属性位于属性列表的位置，如果该位置没有属性，则默认添加新的属性
     * @param attr      新的属性名称
     */
    private static void modifyAttDB(String name, int attrOrder, String attr) {
        try {
            getAttrList(name).set(attrOrder, attr);
            System.out.println("<INFO> modifyAttDataBase:Successfully changed attr");
        } catch (IndexOutOfBoundsException e) {
            try {
                getAttrList(name).add(attr);
                System.out.println("<INFO> modifyAttDataBase:Successfully added attr to position "
                        + getAttrList(name).size()
                        + "in AttrList");
            } catch (Exception e1) {
                System.err.println("<ERROR> modifyAttDataBase:Modify attr failed");
                e1.printStackTrace();
            }
        } catch (Exception e1) {
            System.err.println("<ERROR> modifyAttDataBase:Modify attr failed");
            e1.printStackTrace();
        }
    }

    /**
     * 打印指定数据库的属性栏
     */
    private static String attDBToString(String name) throws
            NoDataBaseExistException,
            AttributeException,
            EmptyDataBaseNameException {
        return getAttrList(name).toString();
    }

    /**
     * 获得 TarList 的轮子
     *
     * @return TarList
     */
    public static ArrayList<String> getTarList(String name) throws
            NoDataBaseExistException,
            EmptyDataBaseNameException,
            AttributeException {
        if (getDataBase(name).get("Tar").get("Tar").size() >= 1) {
            System.out.println(getTime() +
                    "<INFO>getTarList:Successfully get Tar <" +
                    getDataBase(name).get("Tar").get("Tar") + "> form Database <" + name + ">");
        } else {
            System.out.println(getTime() +
                    "<INFO>getTarList:Successfully get getTarList form Database <" +
                    name + ">,but that's an empty list");
        }
        return getDataBase(name).get("Tar").get("Tar");
    }

    /**
     * 检测 输入的 Tar 是否有效
     */
    public static boolean verifyTarExist(String name, String tar) throws
            NoExistTargetException,
            NoDataBaseExistException,
            EmptyDataBaseNameException,
            AttributeException {
        if (getTarList(name).contains(tar)) {
            return true;
        } else {
            System.err.println(getTime() + "<ERROR>verifyTarExist:Non-existent Target <" + tar + ">");
            throw new NoExistTargetException(tar);
        }
    }

    /**
     * 获得 ValueList
     */
    public static ArrayList<String> getValueListByTar(String name, String tar) throws
            NoExistTargetException,
            NoDataBaseExistException,
            EmptyDataBaseNameException,
            AttributeException {
        if (getTarList(name).contains(tar)) {
            if (getDataBase(name).get("Value").get(tar) != null) {
                return getDataBase(name).get("Value").get(tar);
            } else {
                System.err.println(getTime() + "<ERROR>getValueListByTar:Non-existent Target <" + tar + ">");
                throw new NoExistTargetException(tar);
            }
        } else {
            System.err.println(getTime() + "<ERROR>getValueListByTar:Non-existent Target <" + tar + ">");
            throw new NoExistTargetException(tar);
        }
    }

    /**
     * 获取 KeyMap
     */
    public static HashMap<String, ArrayList<String>> getKeyMap(String name) throws
            NoDataBaseExistException,
            EmptyDataBaseNameException,
            KeyMapUninitialException,
            AttributeException {
        if (getDataBase(name).get("Key").size() > 0) {
            return getDataBase(name).get("Key");
        } else {
            System.err.println(getTime() + "<ERROR>getKeyMap:Non-existent KeyMap");
            throw new KeyMapUninitialException();
        }
    }

    /**
     * 获得 KeyList
     */
    public static ArrayList<String> getKeyList(String name) throws
            NoDataBaseExistException,
            EmptyDataBaseNameException,
            KeyMapUninitialException,
            AttributeException {
        if (getKeyMap(name).get("Key").size() >= 1) {
            System.out.println(getTime() +
                    "<INFO>getKeyList:Successfully get KeyList <" +
                    DB.get(name).get("Key").get("Key") + "> form Database <" + name + ">");
        } else {
            System.out.println(getTime() +
                    "<INFO>getKeyList:Successfully get KeyList form Database <" +
                    name + ">,but that's an empty list");
        }
        return getDataBase(name).get("Key").get("Key");
    }

    /**
     * 获取 reference 即, 数据库说明
     */
    public static String getReference(String name) throws
            NoDataBaseExistException,
            NoExistReferenceException,
            KeyMapUninitialException,
            EmptyDataBaseNameException,
            AttributeException {
        if (getKeyMap(name).get("Ref") != null) {
            return getKeyMap(name).get("Ref").toString();
        } else {
            throw new NoExistReferenceException();
        }
    }

    /**
     * 获得 Key 在 KeyList中的位置
     *
     * @param name 数据库名字
     * @param key  Key的名字
     * @return 对应的 key 在 KeyList 中的位置
     */
    public static int getKeyPosition(String name, String key) throws
            NoDataBaseExistException,
            EmptyDataBaseNameException,
            NoExistKeyException,
            KeyMapUninitialException,
            AttributeException {
        if (getKeyList(name).contains(key)) {
            return getKeyList(name).indexOf(key);
        } else {
            System.err.println(getTime() + "<ERROR>getKeyPosition:Non-existent Key <" + key + ">");
            throw new NoExistKeyException(key);
        }
    }

    /**
     * 修改数据库说明
     *
     * @param name 数据库名字
     * @param ref  想要用于更新的 reference 字符串
     */
    public static void setRef(String name, String ref) {
        try {
            getDataBase(name).get("Key").get("Ref").set(0, ref);
            System.out.println(getTime() +
                    "<INFO>setRef:Successfully added reference <" +
                    ref + "> to database <" + name + ">");
        } catch (Exception e) {
            System.err.println(getTime() +
                    "<ERROR>setRef:Unexpected error when addReference to Database <" +
                    name + ">");
            e.printStackTrace();
        }
    }

    /**
     * 添加 Key
     *
     * @param Name 数据库的名字
     * @param key  key的名字
     */
    public static void addKey(String Name, String key) {
        try {
            if (getAttrList(Name).get(0).equals(READ_ONLY)) {
                System.err.println("<ERROR>addKey:Database is Read only, cannot be modified");
            } else {
                getKeyList(Name).add(key);
                if ((getTarList(Name)).size() >= 1) {
                    for (int i = 0; i <= (getTarList(Name)).size() - 1; i++) {
                        getValueListByTar(Name, getTarList(Name).get(i)).add("null");
                    }
                    System.out.println(getTime() + "<INFO>addKey:Successfully add key <" + key + "> to <" + Name + ">");
                }
            }
        } catch (Exception e) {
            System.err.println("<ERROR>addKey:KeyAdd failed!");
            e.printStackTrace();
        }
    }

    /**
     * 添加 Tar
     *
     * @param Name 数据库的名字
     * @param Tar  target的名字
     */
    public static void addTar(String Name, String Tar) {
        try {
            if (getAttrList(Name).get(0).equals(READ_ONLY)) {
                System.err.println("<ERROR>addKey:Database is Read only, cannot be modified");
            } else {
                ArrayList<String> c = new ArrayList<>();
                getTarList(Name).add(Tar);
                if (getKeyList(Name).size() >= 1) {
                    for (int i = 0; i < getKeyList(Name).size(); i++) {
                        c.add("null");
                    }
                }
                getDataBase(Name).get("Value").put(Tar, c);
            }
        } catch (Exception e) {
            System.err.println(getTime() + "<ERROR>addTar:TarAdd failed!");
            e.printStackTrace();
        }
    }

    /**
     * TODO 感觉那些抛出我都覆盖到了，就全删了，你自己查一下。
     */
    public static void addValue(String Name, String Tar, String Key, String Value) {
        try {
            //if (!Name.equals("") & !Tar.equals("") & !Key.equals("")) {
            //    if (!(DB.get(Name) == null)) {
            //        if (DB.get(Name).get("Key").get("Key").indexOf(Key) != -1) {
            //            DB.get(Name).get("Value").get(Tar).set(DB.get(Name).get("Key").get("Key").indexOf(Key), Value);
            //            System.out.println(getTime() + "<INFO>addValue:Successfully set Value <" + Value + "> to <" + Name + " , " + Tar + " , " + Key + ">");
            //        } else {
            //            System.err.println(getTime() + "<ERROR>addValue:Database " + Name + " didnot contain Key " + Key + "!");
            //        }
            //    } else {
            //        System.err.println(getTime() + "<ERROR>addValue:Database " + Name + " non-existent!");
            //    }
            //} else {
            //    System.err.println(getTime() + "<ERROR>addValue:Incorrect parameter!");
            //}
            getValueListByTar(Name, Tar).set(getKeyPosition(Name, Key), Value);
            System.out.println(getTime() + "<INFO>addValue:Successfully set Value <" + Value + "> to <" + Name + " , " + Tar + " , " + Key + ">");
        } catch (Exception e) {
            System.err.println(getTime() + "<ERROR>addValue:ValueAdd failed!");
            e.printStackTrace();
        }
    }

    public static boolean Verify(String Name) {
        try {
            if (DB.get(Name).get("Tar").get("Tar").isEmpty() & !DB.get(Name).get("Value").isEmpty()) {
                System.err.println(getTime() + "<ERROR>DBVerify:Database <" + Name + "> is broken!");
                return true;
            }
            for (int a = 0; a <= DB.get(Name).get("Tar").get("Tar").size() - 1; a++) {
                if (DB.get(Name).get("Value").get(DB.get(Name).get("Tar").get("Tar").get(a)).size() != DB.get(Name).get("Key").get("Key").size()) {
                    System.err.println(getTime() + "<ERROR>DBVerify:Database <" + Name + "> is broken!");
                    return false;
                }
            }
            System.out.println(getTime() + "<INFO>DBVerify:Database <" + Name + "> is successfully verified!");
            return true;
        } catch (Exception e) {
            System.err.println(getTime() + "<ERROR>:DBVerify:Unxepected error when verifying DB <" + Name + ">");
            e.printStackTrace();
            return false;
        }
    }

    public static void printDB(String Name) {
        if (DB.get(Name) != null) {
            try {
                int counter = 0;
                System.out.println("DBprinter:Now printing database <" + Name + ">");
                System.out.println("Reference:" + DB.get(Name).get("Key").get("Ref"));
                System.out.println("==================");
                System.out.println("<Tag>" + DB.get(Name).get("Key").get("Key"));
                for (int i = 0; i <= DB.get(Name).get("Tar").get("Tar").size() - 1; i++) {
                    System.out.println("<" + DB.get(Name).get("Tar").get("Tar").get(i) + ">" + DB.get(Name).get("Value").get(DB.get(Name).get("Tar").get("Tar").get(i)));
                    counter = counter + DB.get(Name).get("Value").get(DB.get(Name).get("Tar").get("Tar").get(i)).size();
                    counter = counter + getValueListByTar(Name, getTarList(Name).get(i)).size();
                }
                System.out.println("==================");
                System.out.println(Fmt.format(new Date()) + "<INFO>DBprinter:Successfully printed <" + Name + ">, " + counter + "Value were installed.");
                System.out.println(getTime() + "<INFO>DBprinter:Successfully printed <" + Name + ">");
            } catch (Exception e) {
                System.err.println(getTime() + "<ERROR>:DBprinter:printDB <" + Name + "> failed!");
                e.printStackTrace();
            }
        } else {
            System.err.println(getTime() + "<ERROR>:DBprinter:Non-existent Database <" + Name + ">");
        }
    }

    /**
     * 补位函数
     * 用于检测参数字符串的长度，并作调整，再打印输出。
     *
     * @param content 输入的字符串
     * @return String 调整长度后的字符串。
     */
    public static String lengthAlert(String content) {
        if (content.length() >= 8) {
            return String.format("%s_", content.substring(0, 7));
        } else {
            StringBuilder text = new StringBuilder();
            text.append(content);
            for (int i = 8 - content.length(); i > 0; i--) {
                text.append(" ");
            }
            return text.toString();
        }
    }

    /**
     * 重写 toString() 方法
     * TODO  添加 Try block
     */
    public static String dataBaseToString() throws
            EmptyDataBaseNameException,
            KeyMapUninitialException,
            NoDataBaseExistException,
            NoExistReferenceException,
            NoExistTargetException,
            AttributeException {
        StringBuilder wareHouseS = new StringBuilder();
        wareHouseS.append("\n<Out> Now print all DataBase\n");
        // 遍历所有dataBase
        for (Map.Entry<String, HashMap<String, HashMap<String, ArrayList<String>>>> entry : DB.entrySet()) {
            if (!Verify(entry.getKey())) {
                wareHouseS.append("==============================================\n");
                wareHouseS.append(String.format("<Error> Database [%s] has some problem.\n",
                        entry.getKey()));
            }
            // 间隔符
            wareHouseS.append("==============================================\n");
            // dataBase 名字和创建时间
            wareHouseS.append(String.format("[%s] Description: %s\n", entry.getKey(),
                    getReference(entry.getKey())));
            // 打印数据库目前的属性栏
            wareHouseS.append("# Attribute:");
            for (String attr : getAttrList(entry.getKey())) {
                wareHouseS.append(String.format("%s.", attr));
            }
            wareHouseS.append("\n");
            // 遍历所有 key 作为表格第一行
            wareHouseS.append("________| ");
            for (String keys : getKeyList(entry.getKey())) {
                wareHouseS.append(String.format("%s ", lengthAlert(keys)));
            }
            wareHouseS.append("\n");
            // 遍历Tar的长度（这里Tar的长度应该和dataBase.的外层长度相同。
            for (int i = 0; i < getTarList(entry.getKey()).size(); i++) {
                wareHouseS.append(lengthAlert(getTarList(entry.getKey()).get(i)));
                wareHouseS.append("| ");
                for (String value : getValueListByTar(entry.getKey(), getTarList(entry.getKey()).get(i))) {
                    wareHouseS.append(String.format("%s ", lengthAlert(value)));
                }
                wareHouseS.append("\n");
            }
        }
        return wareHouseS.toString();
    }

    public static String getValue(String Name, String Tar, String Key) {
        try {
            String value = getValueListByTar(Name, Tar).get(getKeyPosition(Name, Key));
            System.out.println(getTime() + "<INFO>getValue:Successfully getValue <" + value + "> from <" + Name + "," + Tar + "," + Key + ">");
            return value;
        } catch (Exception e) {
            System.err.println(getTime() + "<ERROR>getValue:Failed getValue <" + Name + "," + Tar + "," + Key + ">");
            e.printStackTrace();
            return null;
        }
    }


    public static ArrayList<String> getLine(String Name, String Tar) {
        try {
            System.out.println(getTime() + "<INFO>getLine:Successfully get List <" + getValueListByTar(Name, Tar) + "> from <" + Name + "," + Tar + ">");
            return getValueListByTar(Name, Tar);
        } catch (Exception e) {
            System.err.println(getTime() + "<ERROR>:Unexception error when getLine from <" + Name + "," + Tar + ">");
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<String> getRow(String Name, String Key) {
        try {
            ArrayList<String> res = new ArrayList<>();
            for (String tar : getTarList(Name)) {
                res.add(getValueListByTar(Name, tar).get(getKeyPosition(Name, Key)));
            }
            System.out.println(getTime() + "<INFO>getRow:Successfully get List <" + res + "> from <" + Name + "," + Key + ">");
            return res;
        } catch (Exception e) {
            System.err.println(getTime() + "<ERROR>getRow:Unexcption error when getRow from <" + Name + "," + Key + ">");
            return null;
        }
    }

    public static String getRef(String Name) {
        try {
            System.out.println(getTime() + "<INFO>getReference:Successfully get Reference <" + getReference(Name) + "> from Database <" + Name + ">");
            return getReference(Name);
        } catch (Exception e) {
            System.err.println(getTime() + "<ERROR>getReference:Unexception error when getReference from Database <" + Name + ">");
            return null;
        }
    }

    public static void main(String[] args) throws
            NoDataBaseExistException,
            EmptyDataBaseNameException,
            KeyMapUninitialException,
            NoExistTargetException,
            NoExistReferenceException,
            AttributeException {
        ArrayList<String> test = new ArrayList<>();
        ArrayList<String> test2 = new ArrayList<>();
        BuildDB("Carole", test);//建库   1
        addTar("Carole", "aTar");//加对象   2
        addKey("Carole", "key1");//加类别   3
        setRef("Carole", "Nothing but just a reference.");//加描述   4
        addValue("Carole", "aTar", "key1", "a Value");//加值   5
        addKey("Carole", "key2");
        addTar("Carole", "nextTar");
        addKey("Carole", "third key");
        Verify("Carole");//校验   6
        printDB("Carole");//控制台打印   7
        getTarList("Carole");//获得对象列表   8
        getKeyList("Carole");//获得类别列表   9
        getValue("Carole", "aTar", "key1");//获得单个值   10
        getLine("Carole", "aTar");//获得一整行(指定对象所有值)   11
        getRow("Carole", "key1");//获得一整列(指定类别所有值)   12
        getRef("Carole");//获得描述   13

        BuildDB("Carole!", test2);//建库   1
        addTar("Carole!", "OneTar");//加对象   2
        addKey("Carole!", "FirstKey");//加类别   3
        setRef("Carole!", "Created by Ling~");//加描述   4
        addValue("Carole!", "OneTar", "FirstKey", "a Value");//加值   5
        addKey("Carole!", "SecondKey");
        addTar("Carole!", "TwoTar");
        addTar("Carole!", "ThreeTar");
        addKey("Carole!", "ThirdKey");
        addKey("Carole!", "ForthKey");
        Verify("Carole!");//校验   6
        printDB("Carole!");//控制台打印   7
        getTarList("Carole!");//获得对象列表   8
        getKeyList("Carole!");//获得类别列表   9
        getValue("Carole!", "OneTar", "FirstKey");//获得单个值   10
        getLine("Carole!", "OneTar");//获得一整行(指定对象所有值)   11
        getRow("Carole!", "FirstKey");//获得一整列(指定类别所有值)   12
        getRef("Carole!");//获得描述   13

        modifyAttDB("Carole", 0, READ_ONLY);
        modifyAttDB("Carole!", 1, "Att2");
        System.out.println(dataBaseToString());
    }
}



/*
class EmptyDataBaseNameException extends Exception{

    public EmptyDataBaseNameException () {
        super();
    }

    public EmptyDataBaseNameException (String message) {
        super(message);
    }
}

class NoExistKeyException extends Exception{

    public NoExistKeyException () {
        super();
    }

    public NoExistKeyException (String name) {
        super(name);
    }
}

class NoDataBaseExistException extends Exception {

    public NoDataBaseExistException() {
        super();
    }

    public NoDataBaseExistException(String name) {
        super(name);
    }
}

class NoExistTargetException extends Exception {

    public NoExistTargetException () {
        super();
    }

    public NoExistTargetException (String name) {
        super(name);
    }
}
class NoExistReferenceException extends Exception {

    public NoExistReferenceException () {
        super();
    }

    public NoExistReferenceException (String message) {
        super(message);
    }
}
class KeyMapUninitialException extends Exception {

    public KeyMapUninitialException () {
        super();
    }

    public KeyMapUninitialException (String message) {
        super(message);
    }
}
class AttributeException extends Exception{

    public AttributeException() {
        super();
    }

    public AttributeException(String message) {
        super(message);
    }
}
*/

