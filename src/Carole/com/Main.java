package Carole.com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {

    /**
     * 总库
     * 其下包含多个数据库
     */
    public static HashMap<String, HashMap<String, HashMap<String, ArrayList<String>>>> DB = new HashMap<>();

    /**
     * 时间反馈
     */
    static SimpleDateFormat Fmt = new SimpleDateFormat("yy/MM/dd HH:mm:ss:ms");

    /**
     * 获得 dataBase 的轮子
     *
     * @return Database
     */
    public static HashMap<String, HashMap<String, ArrayList<String>>> getDataBase(String name) throws
            NoDataBaseExistException,
            EmptyDataBaseNameException {
        if (name.equals("")) {
            throw new EmptyDataBaseNameException();
        }
        if (DB.containsKey(name)) {
            return DB.get(name);
        } else {
            System.err.println(Fmt.format(new Date()) + "<ERROR>getValue:Non-existent Database <" + name + ">");
            throw new NoDataBaseExistException(name);
        }
    }

    /**
     * 获得 TarList 的轮子
     *
     * @return TarList
     */
    public static ArrayList<String> getTarList(String name) throws
            NoDataBaseExistException,
            EmptyDataBaseNameException {
        if (getDataBase(name).get("Tar").get("Tar").size() >= 1) {
            System.out.println(Fmt.format(new Date()) +
                    "<INFO>getTarList:Successfully get Tar <" +
                    getDataBase(name).get("Tar").get("Tar") + "> form Database <" + name + ">");
        } else {
            System.out.println(Fmt.format(new Date()) +
                    "<INFO>getTarList:Successfully get getTarList form Database <" +
                    name + ">,but that\\\\'s an empty list");
        }
        return getDataBase(name).get("Tar").get("Tar");
    }

    /**
     * 检测 输入的 Tar 是否有效
     */
    public static boolean verifyTarExist(String name, String tar) throws
            NoExistTargetException,
            NoDataBaseExistException,
            EmptyDataBaseNameException {
        if (getTarList(name).contains(tar)) {
            return true;
        } else {
            System.err.println(Fmt.format(new Date()) + "<ERROR>verifyTarExist:Non-existent Target <" + tar + ">");
            throw new NoExistTargetException(tar);
        }
    }

    /**
     * 获得 ValueList 的轮子
     */
    public static ArrayList<String> getValueListByTar(String name, String tar) throws
            NoExistTargetException,
            NoDataBaseExistException,
            EmptyDataBaseNameException {
        if (getTarList(name).contains(tar)) {
            if (getDataBase(name).get("Value").get(tar) != null) {
                return getDataBase(name).get("Value").get(tar);
            } else {
                System.err.println(Fmt.format(new Date()) + "<ERROR>getValueListByTar:Non-existent Target <" + tar + ">");
                throw new NoExistTargetException(tar);
            }
        } else {
            System.err.println(Fmt.format(new Date()) + "<ERROR>getValueListByTar:Non-existent Target <" + tar + ">");
            throw new NoExistTargetException(tar);
        }
    }

    /**
     * 获取 KeyMap
     */
    public static HashMap<String, ArrayList<String>> getKeyMap(String name) throws
            NoDataBaseExistException,
            EmptyDataBaseNameException,
            KeyMapUninitialException {
        if (getDataBase(name).get("Key").size() > 0) {
            return getDataBase(name).get("Key");
        } else {
            System.err.println(Fmt.format(new Date()) + "<ERROR>getKeyMap:Non-existent KeyMap");
            throw new KeyMapUninitialException();
        }
    }

    /**
     * 获得 KeyList 的轮子
     */
    public static ArrayList<String> getKeyList(String name) throws
            NoDataBaseExistException,
            EmptyDataBaseNameException,
            KeyMapUninitialException {
        if (getKeyMap(name).get("Key").size() >= 1) {
            System.out.println(Fmt.format(new Date()) +
                    "<INFO>getKey:Successfully get Key <" +
                    DB.get(name).get("Key").get("Key") + "> form Database <" + name + ">");
        } else {
            System.out.println(Fmt.format(new Date()) +
                    "<INFO>getTar:Successfully get KeyList form Database <" +
                    name + ">,but that\\\\'s an empty list");
        }
        return getDataBase(name).get("Key").get("Key");
    }

    /**
     * 获取reference
     */
    public static String getReference(String name) throws
            NoDataBaseExistException,
            NoExistReferenceException,
            KeyMapUninitialException,
            EmptyDataBaseNameException {
        if (getKeyMap(name).get("Ref") != null) {
            return getKeyMap(name).get("Ref").get(0);
        } else {
            throw new NoExistReferenceException();
        }
    }


    /**
     * 获得 Key 在 KeyList中的位置
     */
    public static int getKeyPosition(String name, String key) throws
            NoDataBaseExistException,
            EmptyDataBaseNameException,
            NoExistKeyException,
            KeyMapUninitialException {
        if (getKeyList(name).contains(key)) {
            return getDataBase(name).get("Key").get("Key").indexOf(key);
        } else {
            System.err.println(Fmt.format(new Date()) + "<ERROR>getValue:Non-existent Key <" + key + ">");
            throw new NoExistKeyException(key);
        }
    }


    /**
     * 用于创建一个新的数据库并放到上面的总库中。
     *
     * @param Name 新数据库的名字
     * @param Key  新数据库中的键值
     */
    public static void BuildDB(String Name, ArrayList<String> Key) {//数据库初始化(建库)
        ArrayList<String> AttOrg = new ArrayList(1);
        AttOrg.add("1");
        try {
            // 如果名字不为空
            if (!Name.equals("")) {
                // 新创建一个HashMap，FS，也就是新的数据库
                HashMap<String, HashMap<String, ArrayList<String>>> FS = new HashMap<>();//单个库（目录）
                // SS 作为value的记录Map
                HashMap<String, ArrayList<String>> SS = new HashMap<>();//内矩阵
                // K 作为Key的记录Map
                HashMap<String, ArrayList<String>> K = new HashMap<>();
                // Nm作为Tar的记录Map
                HashMap<String, ArrayList<String>> Nm = new HashMap<>();//对象
                // 新建ArrayList，用于记录Tar，这个只是用来打包Tar
                HashMap<String, ArrayList<String>> At = new HashMap<>();
                HashMap<String, ArrayList<String>> Attt = new HashMap<>();
                HashMap<String, ArrayList<String>> Atttt = new HashMap<>();
                //数据库属性存放处
                Atttt.put("DBAtt", AttOrg);
                FS.put("DBAtt", Atttt);
                for (int i = 0; i <= Key.size() - 1; i++) {
                    At.put(Key.get(i), AttOrg);
                }
                FS.put("KeyAtt", At);
                FS.put("TarAtt", Attt);
                ArrayList<String> T = new ArrayList<>();
                ArrayList<String> Ref = new ArrayList<>(1);
                Ref.add("");
                // 将列表KeyList 放到 Keymap 中
                K.put("Key", Key);
                K.put("Ref", Ref);
                // 将打包的Tar列表放入TarMap中 作为TarMap
                Nm.put("Tar", T);
                // 将 TarMap 放到FSMap中
                FS.put("Tar", Nm);
                // 将 KeyMap 放入FSMap中
                FS.put("Key", K);
                // 将 valueMap 放入FSMap中
                FS.put("Value", SS);
                // 将FSMap 放入总数据库中。
                DB.put(Name, FS);
                System.out.println(Fmt.format(new Date()) +
                        "<INFO>DBbuilder:Successfully built DB <" + Name + ">");
            } else {
                System.err.println("<ERROR>DBbulider:Empty DB name!");
            }
        } catch (Exception e) {
            System.err.println("<ERROR>DBbulider:DB initialization failed!");
            e.printStackTrace();
        }
    }


    //设置描述Reference
    public static void setRef(String Name, String Ref) {
        try {
            if (setableDB(Name) == false) {
                return;
            }
            getDataBase(Name).get("Key").get("Ref").set(0, Ref);
            System.out.println(Fmt.format(new Date()) +
                    "<INFO>setReference:Successfully added reference <" +
                    Ref + "> to database <" + Name + ">");
        } catch (Exception e) {
            System.err.println(Fmt.format(new Date()) +
                    "<ERROR>setReference:Unexpection error when addReference to Database <" +
                    Name + ">");
            e.printStackTrace();
        }
    }

    /**
     * 添加Key
     *
     * @param Name 数据库的名字
     * @param key  key的名字
     */
    public static void addKey(String Name, String key) {
        ArrayList<String> AttOrg = new ArrayList(1);
        AttOrg.add("1");
        try {
            if (setableDB(Name) == false) {
                return;
            }
            // 如果key的名字不为空
            if (!key.equals("")) {
                // 这里的get返回对应的Object
                // 于是乎，通过get(key)，获得了底层的Key List, 再把刚刚的key加上去。
                DB.get(Name).get("Key").get("Key").add(key);
                // 如对Name对应的TarMap中TarList的长度大于1
                DB.get(Name).get("KeyAtt").put(key, AttOrg);
                // 即，已经存在一个或以上的Tar
                if ((DB.get(Name).get("Tar").get("Tar")).size() >= 1) {
                    // 循环TarList
                    for (int i = 0; i <= (DB.get(Name).get("Tar").get("Tar")).size() - 1; i++) {
                        // 为每一个ValueMap 中对应的Tar 对应的ArrayList添加一个 "null", 作为初始化。
                        //DB.get(Name).get("Value").get((DB.get(Name).get("Tar").get("Tar")).get(i)) = new ArrayList<>();
                        DB.get(Name).get("Value").get((DB.get(Name).get("Tar").get("Tar")).get(i)).add("null");
                        //getValueListByTar(Name, getTarList(Name).get(i)).add("null");
                    }
                    System.out.println(Fmt.format(new Date()) + "<INFO>DBkey:Successfully add key <" + key + "> to <" + Name + ">");
                }
            } else {
                System.err.println("<ERROR>DBkey:Empty Key name!");
            }
        } catch (Exception e) {
            System.err.println("<ERROR>DBkey:KeyAdd failed!");
            e.printStackTrace();
        }
    }


    //添加对象Target
    public static void addTar(String Name, String Tar) {
        ArrayList<String> AttOrg = new ArrayList(1);
        AttOrg.add("1");
        try {
            if (setableDB(Name) == false) {
                return;
            }
            // 如果参数 Tar不为空
            if (!Tar.equals("")) {
                // 创建 cList
                ArrayList<String> c = new ArrayList<>();
                DB.get(Name).get("TarAtt").put(Tar, AttOrg);
                // 将参数Tar添加到Name对应DB下的TarList
                DB.get(Name).get("Tar").get("Tar").add(Tar);
                // 如果Name对应DB下的keyList不为空，则遍历KeyList中的每一个数值，并向c中添加“null”
                if (DB.get(Name).get("Key").get("Key").size() >= 1) {
                    for (int i = 0; i <= DB.get(Name).get("Key").get("Key").size() - 1; i++) {
                        c.add("null");
                    }
                }
                // 在ValueMap中创建新的Entry，<Tar, cuo>
                DB.get(Name).get("Value").put(Tar, c);
                System.out.println(Fmt.format(new Date()) + "<INFO>DBkey:Successfully add target <" + Tar + "> to <" + Name + ">");
            } else {
                System.err.println(Fmt.format(new Date()) + "<ERROR>DBTar:Empty Tar name!");
            }
        } catch (Exception e) {
            System.err.println(Fmt.format(new Date()) + "<ERROR>DBTar:TarAdd failed!");
            e.printStackTrace();
        }
    }


    //加值Value
    public static void addValue(String Name, String Tar, String Key, String Value) {
        try {
            if (setableDB(Name) == false | setableKey(Name, Key) == false | setableTar(Name, Tar) == false) {
                return;
            }
            if (!Name.equals("") & !Tar.equals("") & !Key.equals("")) {
                if (!(DB.get(Name) == null)) {
                    if (DB.get(Name).get("Key").get("Key").indexOf(Key) != -1) {
                        DB.get(Name).get("Value").get(Tar).set(DB.get(Name).get("Key").get("Key").indexOf(Key), Value);
                        System.out.println(Fmt.format(new Date()) + "<INFO>DBkey:Successfully set Value <" + Value + "> to <" + Name + " , " + Tar + " , " + Key + ">");
                    } else {
                        System.err.println(Fmt.format(new Date()) + "<ERROR>DBValue:Database " + Name + " didnot contain Key " + Key + "!");
                    }
                } else {
                    System.err.println(Fmt.format(new Date()) + "<ERROR>DBValue:Database " + Name + " non-existent!");
                }
            } else {
                System.err.println(Fmt.format(new Date()) + "<ERROR>DBValue:Incorrect parameter!");
            }
        } catch (Exception e) {
            System.err.println(Fmt.format(new Date()) + "<ERROR>DBValue:ValueAdd failed!");
            e.printStackTrace();
        }
    }


    //数据库校验Verify
    public static boolean Verify(String Name) {
        try {
            if (DB.get(Name).get("KeyAtt").size() != DB.get(Name).get("Key").get("Key").size()) {
                System.err.println(Fmt.format(new Date()) + "<ERROR>DBVerify:Database <" + Name + "> is broken!");
                return false;
            }
            if (DB.get(Name).get("TarAtt").size() != DB.get(Name).get("Tar").get("Tar").size()) {
                System.err.println(Fmt.format(new Date()) + "<ERROR>DBVerify:Database <" + Name + "> is broken!");
                return false;
            }
            if (DB.get(Name).get("Tar").get("Tar").isEmpty() & !DB.get(Name).get("Value").isEmpty()) {
                System.err.println(Fmt.format(new Date()) + "<ERROR>DBVerify:Database <" + Name + "> is broken!");
                return false;
            }
            for (int a = 0; a <= DB.get(Name).get("Tar").get("Tar").size() - 1; a++) {
                if (DB.get(Name).get("Value").get(DB.get(Name).get("Tar").get("Tar").get(a)).size() != DB.get(Name).get("Key").get("Key").size()) {
                    System.err.println(Fmt.format(new Date()) + "<ERROR>DBVerify:Database <" + Name + "> is broken!");
                    return false;
                }
            }
            System.out.println(Fmt.format(new Date()) + "<INFO>DBVerify:Database <" + Name + "> is successfully verified!");
            return true;
        } catch (Exception e) {
            System.err.println(Fmt.format(new Date()) + "<ERROR>:DBVerify:Unxepected error when verifying DB <" + Name + ">");
            e.printStackTrace();
            return false;
        }
    }


    //控制台打印输出:Tips:此方法不受Attribute约束
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
                }
                System.out.println("==================");
                String KeyAttribute = "KeyAttribute: ";
                System.out.println("DBAttribute: " + DB.get(Name).get("DBAtt").get("DBAtt").get(0));
                for (int a = 0; a <= DB.get(Name).get("Key").get("Key").size() - 1; a++) {
                    KeyAttribute = KeyAttribute + DB.get(Name).get("Key").get("Key").get(a) + ":" + DB.get(Name).get("KeyAtt").get(DB.get(Name).get("Key").get("Key").get(a)).get(0) + ", ";
                }
                System.out.println(KeyAttribute);
                String TarAttribute = "TarAttribute: ";
                for (int a = 0; a <= DB.get(Name).get("Tar").get("Tar").size() - 1; a++) {
                    TarAttribute = TarAttribute + DB.get(Name).get("Tar").get("Tar").get(a) + ":" + DB.get(Name).get("TarAtt").get(DB.get(Name).get("Tar").get("Tar").get(a)).get(0) + ", ";
                }
                System.out.println(TarAttribute);
                System.out.println(Fmt.format(new Date()) + "<INFO>DBprinter:Successfully printed <" + Name + ">, " + counter + " Value were installed.");
                System.out.println("Tips:Attributes: 1 ->Accessable ; 0 ->ReadOnly ; -1 ->Lock");
                return;
            } catch (Exception e) {
                System.err.println(Fmt.format(new Date()) + "<ERROR>:DBprinter:printDB <" + Name + "> failed!");
                e.printStackTrace();
                return;
            }
        } else {
            System.err.println(Fmt.format(new Date()) + "<ERROR>:DBprinter:Non-existent Database <" + Name + ">");
            return;
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
            // 0123456789
            // 0123456_|
            return String.format("%s_", content.substring(0, 6));
        } else {
            StringBuilder text = new StringBuilder();
            // 0123
            // 0123____|
            text.append(content);
            for (int i = 0; i < 8 - content.length(); i++) {
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
            //wareHouseS.append("# Attribute:");
            //for (String attr : getAttrList(entry.getKey())) {
            //    wareHouseS.append(String.format("%s.", attr));
            //}
            //wareHouseS.append("\n");
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


    //获得值Value
    public static String getValue(String Name, String Tar, String Key) {
        try {
            if (!readableDB(Name) | !readableKey(Name, Key) | !readableTar(Name, Tar)) {
                return null;
            }
            String value = getValueListByTar(Name, Tar).get(getKeyPosition(Name, Key));
            System.out.println(Fmt.format(new Date()) + "<INFO>getValue:Successfully getValue <" + value + "> from <" + Name + "," + Tar + "," + Key + ">");
            return value;
        } catch (Exception e) {
            System.err.println(Fmt.format(new Date()) + "<ERROR>getValue:Failed getValue <" + Name + "," + Tar + "," + Key + ">");
            e.printStackTrace();
            return null;
        }
    }


    //获得一整行（对应对象所有值）
    public static ArrayList<String> getLine(String Name, String Tar) {
        try {
            if (!readableDB(Name) | !readableKeyList(Name) | !readableTar(Name, Tar)) {
                return null;
            }
            System.out.println(Fmt.format(new Date()) + "<INFO>getLine:Successfully get List <" + getValueListByTar(Name, Tar) + "> from <" + Name + "," + Tar + ">");
            return getValueListByTar(Name, Tar);
        } catch (Exception e) {
            System.err.println(Fmt.format(new Date()) + "<ERROR>:Unexception error when getLine from <" + Name + "," + Tar + ">");
            e.printStackTrace();
            return null;
        }
    }


    //获得一整列（对应键值所有值）
    public static ArrayList<String> getRow(String Name, String Key) {
        try {
            if (!readableDB(Name) | !readableKey(Name, Key) | !readableTarList(Name)) {
                return null;
            }
            ArrayList<String> res = new ArrayList();
            for (String tar : getTarList(Name)) {
                res.add(getValueListByTar(Name, tar).get(getKeyPosition(Name, Key)));
            }
            System.out.println(Fmt.format(new Date()) + "<INFO>getRow:Successfully get List <" + res + "> from <" + Name + "," + Key + ">");
            return res;
        } catch (Exception e) {
            System.err.println(Fmt.format(new Date()) + "<ERROR>getRow:Unexcption error when getRow from <" + Name + "," + Key + ">");
            return null;
        }
    }


    //获得描述Reference
    public static String getRef(String Name) {
        try {
            if (!readableDB(Name)) {
                return null;
            }
            System.out.println(Fmt.format(new Date()) + "<INFO>getReference:Successfully get Reference <" + getReference(Name) + "> from Database <" + Name + ">");
            return getReference(Name);
        } catch (Exception e) {
            System.err.println(Fmt.format(new Date()) + "<ERROR>getReference:Unexception error when getReference from Database <" + Name + ">");
            return null;
        }
    }

    //删除指定数据库
    public static void removeDB(String Name) {
        try {
            if (!setableDB(Name) | !setableKeyList(Name) | !setableTarList(Name)) {
                return;
            }
            if (DB.get(Name) != null) {
                DB.remove(Name);
                System.out.println(Fmt.format(new Date()) + "<INFO>removeDB:Successfully removed Database <" + Name + ">");
            } else {
                System.err.println(Fmt.format(new Date()) + "<ERROR>removeDB:Non-existent Database <" + Name + ">");
            }
        } catch (Exception e) {
            System.err.println(Fmt.format(new Date()) + "<ERROR>removeDB:Unexception error when remove Database <" + Name + ">");
        }
    }


    //重命名数据库
    public static void renameDB(String Name, String newName) {
        try {
            if (!setableDB(Name)) {
                return;
            }
            if (DB.get(Name) != null) {
                DB.put(newName, DB.get(Name));
                DB.remove(Name);
                System.out.println(Fmt.format(new Date()) + "<INFO>renameDB:Successfully renamed Database <" + Name + "> as <" + newName + ">");
            } else {
                System.err.println(Fmt.format(new Date()) + "<ERROR>renameDB:Non-existent Database <" + Name + ">");
            }
        } catch (Exception e) {
            System.err.println(Fmt.format(new Date()) + "<ERROR>renameDB:Unexception error when rename Database <" + Name + ">");
        }
    }


    //清除数据库对应对象，键值下某个值
    public static void clearValue(String Name, String Tar, String Key) {
        try {
            if (!setableDB(Name) | !setableKey(Name, Key) | !setableTar(Name, Tar)) {
                return;
            }
            if (DB.get(Name) != null) {
                if (DB.get(Name).get("Key").get("Key").indexOf(Key) == -1) {
                    System.err.println(Fmt.format(new Date()) + "<ERROR>clearValue:Non-existent Key <" + Key + "> in Database <" + Name + ">");
                    return;
                }
                if (DB.get(Name).get("Tar").get("Tar").indexOf(Tar) == -1) {
                    System.err.println(Fmt.format(new Date()) + "<ERROR>clearValue:Non-existent Tar <" + Tar + "> in Database <" + Name + ">");
                    return;
                }
                DB.get(Name).get("Value").get(Tar).set(DB.get(Name).get("Key").get("Key").indexOf(Key), "null");
                System.out.println(Fmt.format(new Date()) + "<INFO>clearValue:Successfully clearValue <" + Name + "," + Tar + "," + Key + ">");
            } else {
                System.err.println(Fmt.format(new Date()) + "<ERROR>clearValue:Non-existent Database <" + Name + "," + Tar + "," + Key + ">");
            }
        } catch (Exception e) {
            System.err.println(Fmt.format(new Date()) + "<ERROR>clearValue:Unexception error when clear Value <" + Name + "," + Tar + "," + Key + ">");
        }
    }


    //检测键值存在与否
    public static boolean ifKeyExist(String Name, String Key) {
        try {
            if (DB.get(Name) != null) {
                for (int i = 0; i <= DB.get(Name).get("Key").get("Key").size() - 1; i++) {
                    if (DB.get(Name).get("Key").get("Key").get(i).equals(Key)) {
                        System.out.println(Fmt.format(new Date()) + "<INFO>ifKeyExist:Successfully positioned Key <" + Key + "> in Database <" + Name + "> State:TRUE");
                        return true;
                    }
                }
                System.out.println(Fmt.format(new Date()) + "<INFO>ifKeyExist:Successfully positioned Key <" + Key + "> in Database <" + Name + "> State:FALSE");
                return false;
            } else {
                System.err.println(Fmt.format(new Date()) + "<ERROR>ifKeyExist:Non-existent Database <" + Name + ">");
                return false;
            }
        } catch (Exception e) {
            System.err.println(Fmt.format(new Date()) + "<ERROR>ifKeyExist:Unexception error when position Key <" + Key + "> Database <" + Name + ">");
            return false;
        }
    }


    //检测对象存在与否
    public static boolean ifTarExist(String Name, String Tar) {
        try {
            if (DB.get(Name) != null) {
                for (int i = 0; i <= DB.get(Name).get("Tar").get("Tar").size() - 1; i++) {
                    if (DB.get(Name).get("Tar").get("Tar").get(i).equals(Tar)) {
                        System.out.println(Fmt.format(new Date()) + "<INFO>ifTarExist:Successfully positioned Target <" + Tar + "> in Database <" + Name + "> State:TRUE");
                        return true;
                    }
                }
                System.out.println(Fmt.format(new Date()) + "<INFO>ifTarExist:Successfully positioned Target <" + Tar + "> in Database <" + Name + "> State:FALSE");
                return false;
            } else {
                System.err.println(Fmt.format(new Date()) + "<ERROR>ifTarExist:Non-existent Database <" + Name + ">");
                return false;
            }
        } catch (Exception e) {
            System.err.println(Fmt.format(new Date()) + "<ERROR>ifTarExist:Unexception error when position Target <" + Tar + "> Database <" + Name + ">");
            return false;
        }
    }


    //检测数据库存在与否
    public static boolean ifDBExist(String Name) {
        try {
            if (DB.get(Name) != null) {
                System.out.println(Fmt.format(new Date()) + "<INFO>ifDBexist:Successfully positioned Database <" + Name + "> State:TRUE");
                return true;
            } else {
                System.out.println(Fmt.format(new Date()) + "<INFO>ifDBexist:Successfully positioned Database <" + Name + "> State:FALSE");
                return false;
            }
        } catch (Exception e) {
            System.err.println(Fmt.format(new Date()) + "<ERROR>ifDBexist:Unexception error when position Database <" + Name + ">");
            return false;
        }
    }


    //清空指定数据库
    public static void clearDB(String Name) {
        if (!setableDB(Name) | !setableKeyList(Name) | !setableTarList(Name)) {
            return;
        }
        removeDB(Name);
        BuildDB(Name, new ArrayList<String>());
        //tips:This function would not throw any Exceptions.
    }

    public static void clearTarList(String Name) {
        try {
            if (DB.get(Name) == null) {
                System.err.println(Fmt.format(new Date()) + "<ERROR>clearTarList:Non-existent Database <" + Name + ">");
                return;
            }
            ArrayList<String> list = new ArrayList();
            list = DB.get(Name).get("Key").get("Key");
            removeDB(Name);
            BuildDB(Name, list);
            System.out.println(Fmt.format(new Date()) + "<INFO>clearTarList:Successfully clearTarList for Database <" + Name + ">");
        } catch (Exception e) {
            System.err.println(Fmt.format(new Date()) + "<ERROR>clearTarList:Unexception error when clearTarList for database <" + Name + ">");
            e.printStackTrace();
        }
    }


    //清空键值列表
    public static void clearKeyList(String Name) {
        try {
            if (!setableDB(Name) | !setableKeyList(Name) | !setableTarList(Name)) {
                return;
            }
            if (DB.get(Name) == null) {
                System.err.println(Fmt.format(new Date()) + "<ERROR>clearKeyList:Non-existent Database <" + Name + ">");
                return;
            } else {
                ArrayList<String> list = new ArrayList();
                list = DB.get(Name).get("Tar").get("Tar");
                removeDB(Name);
                BuildDB(Name, new ArrayList<String>());
                for (int i = 0; i <= list.size() - 1; i++) {
                    addTar(Name, list.get(i));
                }
                System.out.println(Fmt.format(new Date()) + "<INFO>clearKeyList:Successfully clearKeyList for Database <" + Name + ">");
            }
        } catch (Exception e) {
            System.err.println(Fmt.format(new Date()) + "<ERROR>clearKeyList:Unexception error when clearKeyList for database <" + Name + ">");
            e.printStackTrace();
        }
    }


    //清空描述
    public static void clearRef(String Name) {
        try {
            if (!setableDB(Name)) {
                return;
            }
            if (DB.get(Name) == null) {
                System.err.println(Fmt.format(new Date()) + "<ERROR>clearRef:Non-existent Database <" + Name + ">");
                return;
            }
            getKeyMap(Name).get("Ref").set(0, "");
            System.out.println(Fmt.format(new Date()) + "<INFO>clearRef:Successfully clearReference for Database <" + Name + ">");
        } catch (Exception e) {
            System.err.println(Fmt.format(new Date()) + "<ERROR>clearRef:Unexception error when clearReference for Database <" + Name + ">");
            e.printStackTrace();
        }
    }


    //清空数据库下某对象的所有值
    public static void clearLine(String Name, String Tar) {
        try {
            if (!setableDB(Name) | !setableKeyList(Name) | !setableTar(Name, Tar)) {
                return;
            }
            if (DB.get(Name) == null) {
                System.err.println(Fmt.format(new Date()) + "<ERROR>clearLine:Non-existent Database <" + Name + ">");
                return;
            } else if (ifTarExist(Name, Tar) == false) {
                System.err.println(Fmt.format(new Date()) + "<ERROR>clearLine:Non-existent Target <" + Tar + "> in Database <" + Name + ">");
                return;
            }
            for (int i = 0; i <= DB.get(Name).get("Key").get("Key").size() - 1; i++) {
                DB.get(Name).get("Value").get(Tar).set(i, "null");
            }
            System.out.println(Fmt.format(new Date()) + "<INFO>clearLine:Successfully clearLine for <" + Name + "," + Tar + ">");
        } catch (Exception e) {
            System.err.println(Fmt.format(new Date()) + "<ERROR>clearLine:Unexception error when clearLine for <" + Name + "," + Tar + ">");
            e.printStackTrace();
        }
    }


    //清空数据库下某键值的一整列
    public static void clearRow(String Name, String Key) {
        try {
            if (!setableDB(Name) | !setableKey(Name, Key) | !setableTarList(Name)) {
                return;
            }
            if (DB.get(Name) == null) {
                System.err.println(Fmt.format(new Date()) + "<ERROR>clearRow:Non-existent Database <" + Name + ">");
                return;
            } else if (ifKeyExist(Name, Key) == false) {
                System.err.println(Fmt.format(new Date()) + "<ERROR>clearRow:Non-existent Key <" + Key + "> in Database <" + Name + ">");
                return;
            }
            for (int i = 0; i <= DB.get(Name).get("Tar").get("Tar").size() - 1; i++) {
                DB.get(Name).get("Value").get(DB.get(Name).get("Tar").get("Tar").get(i)).set(DB.get(Name).get("Key").get("Key").indexOf(Key), "null");
            }
            System.out.println(Fmt.format(new Date()) + "<INFO>clearRow:Successfully clearRow for <" + Name + "," + Key + ">");
        } catch (Exception e) {
            System.err.println(Fmt.format(new Date()) + "<ERROR>clearRow:Unexception error when clearRow for <" + Name + "," + Key + ">");
            e.printStackTrace();
        }
    }


    //检测数据库可读性
    public static boolean readableDB(String Name) {
        try {
            if (DB.get(Name).get("DBAtt").get("DBAtt").get(0).equals("-1") == false) {
                System.out.println(Fmt.format(new Date()) + "<INFO>readableDB:Accessable Database <" + Name + ">");
                return true;
            } else {
                System.out.println(Fmt.format(new Date()) + "<INFO>readableDB:UNaccessable Database <" + Name + ">");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    //数据库可写性
    public static boolean setableDB(String Name) {
        try {
            if (DB.get(Name).get("DBAtt").get("DBAtt").get(0).equals("1") == true) {
                System.out.println(Fmt.format(new Date()) + "<INFO>setableDB:Accessable Database <" + Name + ">");
                return true;
            } else {
                System.out.println(Fmt.format(new Date()) + "<INFO>setableDB:UNaccessable Database <" + Name + ">");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    //键值可读性
    public static boolean readableKey(String Name, String Key) {
        try {
            if (DB.get(Name).get("KeyAtt").get(Key).get(0).equals("-1") == false) {
                System.out.println(Fmt.format(new Date()) + "<INFO>readableKey:Accessable Key <" + Name + "," + Key + ">");
                return true;
            } else {
                System.out.println(Fmt.format(new Date()) + "<INFO>readableKey:UNaccessable Key <" + Name + "," + Key + ">");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    //键值可写性
    public static boolean setableKey(String Name, String Key) {
        try {
            if (DB.get(Name).get("KeyAtt").get(Key).get(0).equals("1") == true) {
                System.out.println(Fmt.format(new Date()) + "<INFO>setableKey:Accessable Key <" + Name + "," + Key + ">");
                return true;
            } else {
                System.out.println(Fmt.format(new Date()) + "<INFO>setableKey:UNaccessable Key <" + Name + "," + Key + ">");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    //对象可读性
    public static boolean readableTar(String Name, String Tar) {
        try {
            if (DB.get(Name).get("TarAtt").get(Tar).get(0).equals("-1") == false) {
                System.out.println(Fmt.format(new Date()) + "<INFO>readableTar:Accessable Target <" + Name + "," + Tar + ">");
                return true;
            } else {
                System.out.println(Fmt.format(new Date()) + "<INFO>readableTar:UNaccessable Target <" + Name + "," + Tar + ">");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    //对象可写性
    public static boolean setableTar(String Name, String Tar) {
        try {
            if (DB.get(Name).get("TarAtt").get(Tar).get(0).equals("1") == true) {
                System.out.println(Fmt.format(new Date()) + "<INFO>setableTar:Accessable Target <" + Name + "," + Tar + ">");
                return true;
            } else {
                System.out.println(Fmt.format(new Date()) + "<INFO>setableTar:UNaccessable Target <" + Name + "," + Tar + ">");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    //设置数据库只读
    public static void readOnlyDB(String Name) {
        try {
            if (DB.get(Name) == null) {
                System.err.println(Fmt.format(new Date()) + "<ERROR>readOnlyDB:Non-existent Database <" + Name + ">");
                return;
            }
            DB.get(Name).get("DBAtt").get("DBAtt").set(0, "0");
            System.out.println(Fmt.format(new Date()) + "<INFO>readOnlyDB:Successfully changed Attribute of <" + Name + "> to readOnly.");
        } catch (Exception e) {
            System.err.println(Fmt.format(new Date()) + "<ERROR>readOnlyDB:Unexception error when setDBAtt to Database <" + Name + ">");
            e.printStackTrace();
        }
    }


    //设置数据库读写
    public static void accessableDB(String Name) {
        try {
            if (DB.get(Name) == null) {
                System.err.println(Fmt.format(new Date()) + "<ERROR>accessableDB:Non-existent Database <" + Name + ">");
                return;
            }
            DB.get(Name).get("DBAtt").get("DBAtt").set(0, "1");
            System.out.println(Fmt.format(new Date()) + "<INFO>accessableDB:Successfully changed Attribute of <" + Name + "> to Accessable.");
        } catch (Exception e) {
            System.err.println(Fmt.format(new Date()) + "<ERROR>accessableDB:Unexception error when setDBAtt to Database <" + Name + ">");
            e.printStackTrace();
        }
    }


    //设置数据库锁定
    public static void lockDB(String Name) {
        try {
            if (DB.get(Name) == null) {
                System.err.println(Fmt.format(new Date()) + "<ERROR>lockDB:Non-existent Database <" + Name + ">");
                return;
            }
            DB.get(Name).get("DBAtt").get("DBAtt").set(0, "-1");
            System.out.println(Fmt.format(new Date()) + "<INFO>lockDB:Successfully changed Attribute of <" + Name + "> to Lock.");
        } catch (Exception e) {
            System.err.println(Fmt.format(new Date()) + "<ERROR>lockDB:Unexception error when setDBAtt to Database <" + Name + ">");
            e.printStackTrace();
        }
    }


    //键值只读
    public static void readOnlyKey(String Name, String Key) {
        try {
            if (DB.get(Name) == null) {
                System.err.println(Fmt.format(new Date()) + "<ERROR>readOnlyKey:Non-existent Database <" + Name + ">");
                return;
            }
            if (!setableDB(Name)) {
                return;
            }
            if (ifKeyExist(Name, Key) == false) {
                System.err.println(Fmt.format(new Date()) + "<ERROR>readOnlyKey:Non-existent Key <" + Key + ">");
                return;
            }
            DB.get(Name).get("KeyAtt").get(Key).set(0, "0");
            System.out.println(Fmt.format(new Date()) + "<INFO>readOnlyKey:Successfully changed Attribute of <" + Name + "," + Key + "> to readOnly.");
        } catch (Exception e) {
            System.err.println(Fmt.format(new Date()) + "<ERROR>readOnlyKey:Unexception when setDBAtt to <" + Name + "," + Key + ">");
            e.printStackTrace();
        }
    }


    //键值读写
    public static void accessableKey(String Name, String Key) {
        try {
            if (DB.get(Name) == null) {
                System.err.println(Fmt.format(new Date()) + "<ERROR>accessableKey:Non-existent Database <" + Name + ">");
                return;
            }
            if (!setableDB(Name)) {
                return;
            }
            if (ifKeyExist(Name, Key) == false) {
                System.err.println(Fmt.format(new Date()) + "<ERROR>accessableKey:Non-existent Key <" + Key + ">");
                return;
            }
            DB.get(Name).get("KeyAtt").get(Key).set(0, "1");
            System.out.println(Fmt.format(new Date()) + "<INFO>accessableKey:Successfully changed Attribute of <" + Name + "," + Key + "> to Accessable.");
        } catch (Exception e) {
            System.err.println(Fmt.format(new Date()) + "<ERROR>accessableKey:Unexception when setDBAtt to <" + Name + "," + Key + ">");
            e.printStackTrace();
        }
    }

    //键值锁定
    public static void lockKey(String Name, String Key) {
        try {
            if (DB.get(Name) == null) {
                System.err.println(Fmt.format(new Date()) + "<ERROR>lockKey:Non-existent Database <" + Name + ">");
                return;
            }
            if (!setableDB(Name)) {
                return;
            }
            if (ifKeyExist(Name, Key) == false) {
                System.err.println(Fmt.format(new Date()) + "<ERROR>lockKey:Non-existent Key <" + Key + ">");
                return;
            }
            DB.get(Name).get("KeyAtt").get(Key).set(0, "-1");
            System.out.println(Fmt.format(new Date()) + "<INFO>lockKey:Successfully changed Attribute of <" + Name + "," + Key + "> to Lock.");
        } catch (Exception e) {
            System.err.println(Fmt.format(new Date()) + "<ERROR>lockKey:Unexception when setDBAtt to <" + Name + "," + Key + ">");
            e.printStackTrace();
        }
    }


    //对象只读
    public static void readOnlyTar(String Name, String Tar) {
        try {
            if (DB.get(Name) == null) {
                System.err.println(Fmt.format(new Date()) + "<ERROR>readOnlyTar:Non-existent Database <" + Name + ">");
                return;
            }
            if (!setableDB(Name)) {
                return;
            }
            if (ifTarExist(Name, Tar) == false) {
                System.err.println(Fmt.format(new Date()) + "<ERROR>readOnlyTar:Non-existent Target <" + Tar + ">");
                return;
            }
            DB.get(Name).get("TarAtt").get(Tar).set(0, "0");
            System.out.println(Fmt.format(new Date()) + "<INFO>readOnlyTar:Successfully changed Attribute of <" + Name + "," + Tar + "> to readOnly.");
        } catch (Exception e) {
            System.err.println(Fmt.format(new Date()) + "<ERROR>readOnlyTar:Unexception when setDBAtt to <" + Name + "," + Tar + ">");
            e.printStackTrace();
        }
    }


    //对象读写
    public static void accessableTar(String Name, String Tar) {
        try {
            if (DB.get(Name) == null) {
                System.err.println(Fmt.format(new Date()) + "<ERROR>accessableTar:Non-existent Database <" + Name + ">");
                return;
            }
            if (!setableDB(Name)) {
                return;
            }
            if (ifTarExist(Name, Tar) == false) {
                System.err.println(Fmt.format(new Date()) + "<ERROR>accessableTar:Non-existent Target <" + Tar + ">");
                return;
            }
            DB.get(Name).get("TarAtt").get(Tar).set(0, "1");
            System.out.println(Fmt.format(new Date()) + "<INFO>accessableTar:Successfully changed Attribute of <" + Name + "," + Tar + "> to Accessable.");
        } catch (Exception e) {
            System.err.println(Fmt.format(new Date()) + "<ERROR>accessableTar:Unexception when setDBAtt to <" + Name + "," + Tar + ">");
            e.printStackTrace();
        }
    }


    //对象锁定
    public static void lockTar(String Name, String Tar) {
        try {
            if (DB.get(Name) == null) {
                System.err.println(Fmt.format(new Date()) + "<ERROR>lockTar:Non-existent Database <" + Name + ">");
                return;
            }
            if (!setableDB(Name)) {
                return;
            }
            if (ifTarExist(Name, Tar) == false) {
                System.err.println(Fmt.format(new Date()) + "<ERROR>lockTar:Non-existent Tar <" + Tar + ">");
                return;
            }
            DB.get(Name).get("TarAtt").get(Tar).set(0, "-1");
            System.out.println(Fmt.format(new Date()) + "<INFO>lockTar:Successfully changed Attribute of <" + Name + "," + Tar + "> to Lock.");
        } catch (Exception e) {
            System.err.println(Fmt.format(new Date()) + "<ERROR>lockTar:Unexception when setDBAtt to <" + Name + "," + Tar + ">");
            e.printStackTrace();
        }
    }


    //键值列表可读性（全为可读true，否则false，下同）
    public static boolean setableKeyList(String Name) {
        try {
            for (int i = 0; i <= DB.get(Name).get("Key").get("Key").size() - 1; i++) {
                String Key = DB.get(Name).get("Key").get("Key").get(i);
                if (DB.get(Name).get("KeyAtt").get(Key).get(0).equals("1") == false) {
                    System.out.println(Fmt.format(new Date()) + "<INFO>setableKeyList:UNAccessable KeyList <" + Name + ">");
                    return false;
                }
            }
            System.out.println(Fmt.format(new Date()) + "<INFO>setableKeyList:accessable KeyList <" + Name + ">");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    //对象列表可读性
    public static boolean setableTarList(String Name) {
        try {
            for (int i = 0; i <= DB.get(Name).get("Tar").get("Tar").size() - 1; i++) {
                String Tar = DB.get(Name).get("Tar").get("Tar").get(i);
                if (DB.get(Name).get("TarAtt").get(Tar).get(0).equals("1") == false) {
                    System.out.println(Fmt.format(new Date()) + "<INFO>setableTarList:UNAccessable TarList <" + Name + ">");
                    return false;
                }
            }
            System.out.println(Fmt.format(new Date()) + "<INFO>setableTarList:accessable TarList <" + Name + ">");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    //键值列表可写性
    public static boolean readableKeyList(String Name) {
        try {
            for (int i = 0; i <= DB.get(Name).get("Key").get("Key").size() - 1; i++) {
                String Key = DB.get(Name).get("Key").get("Key").get(i);
                if (DB.get(Name).get("KeyAtt").get(Key).get(0).equals("-1") == true) {
                    System.out.println(Fmt.format(new Date()) + "<INFO>readableKeyList:UNAccessable KeyList <" + Name + ">");
                    return false;
                }
            }
            System.out.println(Fmt.format(new Date()) + "<INFO>readableKeyList:accessable KeyList <" + Name + ">");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    //对象列表可写性
    public static boolean readableTarList(String Name) {
        try {
            for (int i = 0; i <= DB.get(Name).get("Tar").get("Tar").size() - 1; i++) {
                String Tar = DB.get(Name).get("Tar").get("Tar").get(i);
                if (DB.get(Name).get("TarAtt").get(Tar).get(0).equals("-1") == true) {
                    System.out.println(Fmt.format(new Date()) + "<INFO>readableTarList:UNAccessable TarList <" + Name + ">");
                    return false;
                }
            }
            System.out.println(Fmt.format(new Date()) + "<INFO>readableTarList:accessable TarList <" + Name + ">");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    //unicode编码
    private static String unicodeEncode(String string) {
        char[] utfBytes = string.toCharArray();
        String unicodeBytes = "";
        for (int i = 0; i < utfBytes.length; i++) {
            String hexB = Integer.toHexString(utfBytes[i]);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes;
    }


    //unicode解码
    private static String unicodeDecode(String string) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(string);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            string = string.replace(matcher.group(1), ch + "");
        }
        return string;
    }


    //保存到外部文件
    public static void save(String Name, String filePath) {
        String out = "";
        try {
            if (DB.get(Name) == null) {
                System.err.println(Fmt.format(new Date()) + "<ERROR>save:Non-existent Database <" + Name + ">");
                return;
            }
            out = out + "<Name>=" + unicodeEncode(Name) + "<Nm>";
            out = out + "<Refer>=" + unicodeEncode(getRef(Name)) + "<Ref>";
            out = out + "<DBAtt>=" + unicodeEncode(DB.get(Name).get("DBAtt").get("DBAtt").get(0)) + "<DA>";
            out = out + "<Key>=";
            for (int i = 0; i <= DB.get(Name).get("Key").get("Key").size() - 1; i++) {
                out = out + unicodeEncode(DB.get(Name).get("Key").get("Key").get(i));
                if (i != DB.get(Name).get("Key").get("Key").size() - 1) {
                    out = out + ",";
                }
            }
            out = out + "<K>";
            out = out + "<KeyAtt>=";
            for (int i = 0; i <= DB.get(Name).get("Key").get("Key").size() - 1; i++) {
                out = out + unicodeEncode(DB.get(Name).get("KeyAtt").get(DB.get(Name).get("Key").get("Key").get(i)).get(0));
                if (i != DB.get(Name).get("Key").get("Key").size() - 1) {
                    out = out + ",";
                }
            }
            out = out + "<KA>";
            out = out + "<Tar>=";
            for (int i = 0; i <= DB.get(Name).get("Tar").get("Tar").size() - 1; i++) {
                out = out + unicodeEncode(DB.get(Name).get("Tar").get("Tar").get(i));
                if (i != DB.get(Name).get("Tar").get("Tar").size() - 1) {
                    out = out + ",";
                }
            }
            out = out + "<T>";
            out = out + "<TarAtt>=";
            for (int i = 0; i <= DB.get(Name).get("Tar").get("Tar").size() - 1; i++) {
                out = out + unicodeEncode(DB.get(Name).get("TarAtt").get(DB.get(Name).get("Tar").get("Tar").get(i)).get(0));
                if (i != DB.get(Name).get("Tar").get("Tar").size() - 1) {
                    out = out + ",";
                }
            }
            out = out + "<TA>";
            out = out + "<Value>=";
            for (int i = 0; i <= DB.get(Name).get("Tar").get("Tar").size() - 1; i++) {
                for (int a = 0; a <= DB.get(Name).get("Key").get("Key").size() - 1; a++) {
                    out = out + unicodeEncode(getValue(Name, getTarList(Name).get(i), getKeyList(Name).get(a)));
                    if (a != DB.get(Name).get("Key").get("Key").size() - 1) {
                        out = out + ",";
                    }
                }
                if (i != DB.get(Name).get("Tar").get("Tar").size() - 1) {
                    out = out + ";";
                }
            }
            out = out + "<V>";
            File file = new File(filePath);
            if (file.exists()) {
                //判断文件是否存在，如果不存在就新建一个txt
                file.createNewFile();
            }
            FileOutputStream output = new FileOutputStream(file);
            output.write(out.getBytes());
            output.flush();
            output.close();
            System.out.println(Fmt.format(new Date()) + "<INFO>save:Successfully saved Database <" + Name + "> to path <" + filePath + ">");
        } catch (Exception e) {
            System.err.println(Fmt.format(new Date()) + "<ERROR>save:Unexception error when save Database <" + Name + ">");
            e.printStackTrace();
        }
    }

    //切割提取
    private static String take(String input, String Head, String End) {
        try {
            Head = "<" + Head + ">=";
            End = "<" + End + ">";
            return (((input.split(Head))[1]).split(End))[0];
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    //从外部文件读取库
    public static String load(String filePath) {
        boolean Overwrite = false;
        try {
            File in = new File(filePath);
            if (in.isFile() && in.exists()) {
                FileInputStream fis = new FileInputStream(in);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                StringBuffer sb = new StringBuffer();
                String inp = null;
                while ((inp = br.readLine()) != null) {
                    sb.append(inp);
                }
                String input = sb.toString();
                String Name = unicodeDecode(take(input, "Name", "Nm"));
                String DBAtt = unicodeDecode(take(input, "DBAtt", "DA"));
                ArrayList<String> KeyList = new ArrayList();
                for (int i = 0; i <= take(input, "Key", "K").split(",").length - 1; i++) {
                    KeyList.add(unicodeDecode(take(input, "Key", "K").split(",")[i]));
                }
                ArrayList<String> TarList = new ArrayList();
                for (int i = 0; i <= take(input, "Tar", "T").split(",").length - 1; i++) {
                    TarList.add(unicodeDecode(take(input, "Tar", "T").split(",")[i]));
                }
                ArrayList<String> KeyAttList = new ArrayList();
                for (int i = 0; i <= take(input, "KeyAtt", "KA").split(",").length - 1; i++) {
                    KeyAttList.add(unicodeDecode(take(input, "KeyAtt", "KA").split(",")[i]));
                }
                ArrayList<String> TarAttList = new ArrayList();
                for (int i = 0; i <= take(input, "TarAtt", "TA").split(",").length - 1; i++) {
                    TarAttList.add(unicodeDecode(take(input, "TarAtt", "TA").split(",")[i]));
                }
                String Backup = Name;
                if (DB.get(Name) != null) {
                    Name = Name + "<Temporary>";
                    Overwrite = true;
                }
                if (KeyList.size() != KeyAttList.size() | TarList.size() != TarAttList.size()) {
                    System.err.println(Fmt.format(new Date()) + "<ERROR>load:Broken Database <" + Name + "> in <" + filePath + "> keyList size <" + KeyList.size() + "> when KeyAttributeList size <" + KeyAttList.size() + "> ,TarList size <" + TarList.size() + "> when TarAttributeList size <" + TarAttList.size() + ">");
                    return null;
                }
                BuildDB(Name, KeyList);
                setDBAtt(Name, DBAtt);
                for (int i = 0; i <= KeyList.size() - 1; i++) {
                    setKeyAtt(Name, KeyList.get(i), KeyAttList.get(i));
                }
                for (int i = 0; i <= TarList.size() - 1; i++) {
                    addTar(Name, TarList.get(i));
                }
                for (int i = 0; i <= TarList.size() - 1; i++) {
                    setTarAtt(Name, TarList.get(i), TarAttList.get(i));
                }
                String[] Val = take(input, "Value", "V").split(";");
                if (Val.length != TarList.size()) {
                    System.err.println(Fmt.format(new Date()) + "<ERROR>load:Broken Database <" + Name + "> in <" + filePath + "> TarList size <" + TarList.size() + "> when ValueMap size <" + Val.length + ">");
                    removeDB(Name);
                    return null;
                }
                for (int i = 0; i <= TarList.size() - 1; i++) {
                    String[] str = Val[i].split(",");
                    if (str.length != KeyList.size()) {
                        removeDB(Name);
                        System.err.println(Fmt.format(new Date()) + "<ERROR>load:Broken Database <" + Name + "> in <" + filePath + "> keyList size <" + KeyList.size() + "> when ValueList size <" + str.length + ">");
                        return null;
                    }
                    for (int a = 0; a <= KeyList.size() - 1; a++) {
                        addValue(Name, TarList.get(i), KeyList.get(a), unicodeDecode(str[a]));
                    }
                }
                setRef(Name, unicodeDecode(take(input, "Refer", "Ref")));
                if (!Verify(Name)) {
                    removeDB(Name);
                    return null;
                }
                if (Overwrite = true) {
                    removeDB(Backup);
                    renameDB(Name, Backup);
                }
                System.out.println(Fmt.format(new Date()) + "<INFO>load:Successfully loaded Database <" + Backup + "> from <" + filePath + ">");
                return Backup;
            } else {
                System.err.println(Fmt.format(new Date()) + "<ERROR>load:Non-existent path <" + filePath + ">");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(Fmt.format(new Date()) + "<ERROR>load:Unexception error when loading Database from <" + filePath + ">");
            return null;
        }
    }


    //设置Tar属性
    private static void setTarAtt(String Name, String Tar, String Att) {
        try {
            if (DB.get(Name) == null) {
                return;
            }
            if (ifTarExist(Name, Tar) == false) {
                return;
            }
            DB.get(Name).get("TarAtt").get(Tar).set(0, Att);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //设置Key属性
    private static void setKeyAtt(String Name, String Key, String Att) {
        try {
            if (DB.get(Name) == null) {
                return;
            }
            if (ifKeyExist(Name, Key) == false) {
                return;
            }
            DB.get(Name).get("KeyAtt").get(Key).set(0, Att);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //设置数据库属性
    private static void setDBAtt(String Name, String Att) {
        try {
            if (DB.get(Name) == null) {
                return;
            }
            DB.get(Name).get("DBAtt").get("DBAtt").set(0, Att);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws
            NoDataBaseExistException,
            EmptyDataBaseNameException,
            KeyMapUninitialException, AttributeException, NoExistTargetException, NoExistReferenceException {
        ArrayList<String> test = new ArrayList<>();

        //写入*6
        BuildDB("Carole", test);//建库   1
        addTar("Carole", "aTar");//加对象   2
        addKey("Carole", "key1");//加类别   3 ,
        setRef("Carole", "Nothing but just a reference.");//加描述   4
        addValue("Carole", "aTar", "key1", "a Value");//加值   5
        addKey("Carole", "key2");
        addTar("Carole", "nextTar");
        addKey("Carole", "third key");
        Verify("Carole");//校验   6

        //获取*6
        getTarList("Carole");//获得对象列表   7
        getKeyList("Carole");//获得类别列表   8
        getValue("Carole", "aTar", "key1");//获得单个值   9
        getLine("Carole", "aTar");//获得一整行(指定对象所有值)   10
        getRow("Carole", "key1");//获得一整列(指定类别所有值)   11
        getRef("Carole");//获得描述   12

        //侦测*4
        printDB("Carole");//控制台打印   13
        ifDBExist("Carole");//侦测数据库是否存在   14
        ifKeyExist("Carole", "key1");//侦测对应Key是否存在   15
        ifTarExist("Carole", "aTar");//侦测对应Tar是否存在   16

        //清除*9
        //removeDB("Carole");  删除DB   17
        //renameDB("Carole","CLR2");  重命名DB   18
        //clearValue("Carole","aTar","key1");   清除变量   19
        //clearDB("Carole");清空数据库   20
        //clearTarList("Carole");清空Target列表   21
        //clearKeyList("Carole");清空Key列表   22
        //clearRef("Carole");清空描述   23
        //clearLine("Carole","aTar");清除指定Tar下所有子项   24
        //clearRow("Carole","key1");清除指定Key下所有子项   25

        //属性*10
        readableDB("Carole");//检测DB可读性  26
        setableDB("Carole");//检测DB可写性   27
        readableKey("Carole", "key1");//检测Key可读性   28
        setableKey("Carole", "key1");//检测Key可写性   29
        readableTar("Carole", "aTar");//检测Tar可读性   30
        setableTar("Carole", "aTar");//检测Tar可写性   31
        setableKeyList("Carole");//检测全体Key可写性   32
        setableTarList("Carole");//检测全体Tar可写性   33
        readableKeyList("Carole");//检测全体KEY可读性   34
        readableTarList("Carole");//检测全体Tar可读性   35

        /*
        //修改属性*9
        readOnlyDB("Carole");//数据库只读   36
        accessableDB("Carole");//数据库读写   37
        lockDB("Carole");//数据库锁定   38
        readOnlyKey("Carole","key1");//Key只读   39
        accessableKey("Carole","key1");//Key读写   40
        lockKey("Carole","key1");//Key锁定   41
        readOnlyTar("Carole","aTar");//Tar只读   42
        accessableTar("Carole","aTar");//Tar读写   43
        lockTar("Carole","aTar");//Tar锁定   44
        */

        //外部文件交互*2
        save("Carole", "DB.txt");//存储数据库到指定路径   45
        load("DB.txt");//从指定路径读取数据库   46
        printDB("Carole");
        //System.out.println(printDB(););
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

*/