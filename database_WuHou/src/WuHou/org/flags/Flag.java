package WuHou.org.flags;

import java.security.KeyPair;
import java.util.ArrayList;

import WuHou.org.attributes.ReWr;
import WuHou.org.util.PermissionDeniedException;

public class Flag {
    /** flag 的名字 */
    private String name;
    /** flag 的编号 */
    private int id;
    /** 读写权限 */
    private ReWr permission;
    /** 属性 */
    private ArrayList<String> attribute;
    /** 创建者 */
    private String author;
    /** 权限密码 */
    private String password = "000000";

    public Flag(String flag) {
        this.name = flag;
        id = 0;
        permission = ReWr.READ_WRITE;
    }

    public Flag(String flag, String author) {
        this.name = flag;
        this.author = author;
        id = 0;
        permission = ReWr.READ_WRITE;
    }

    public Flag(String flag, int id) {
        this.name = flag;
        this.id = id;
        permission = ReWr.READ_WRITE;
    }

    public Flag(String flag, ReWr permission) {
        this.name = flag;
        id = 0;
        this.permission = permission;
    }

    public Flag(String flag, int id, ReWr permission){
        this.name = flag;
        this.id = id;
        this.permission = permission;
    }

    public Flag(String flag, int id, ReWr permission, String author) {
        this.name = flag;
        this.id = id;
        this.permission = permission;
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String newFlag) throws PermissionDeniedException {
        if (getPermission() == ReWr.READ_ONLY || getPermission() == ReWr.LOCK) {
            throw new PermissionDeniedException("<ERROR> setName: You have no permission to do this");
        } else {
            this.name = newFlag;
        }
    }

    public int getId(){
        return id;
    }

    public int updateId() {
        this.id = this.id + 1;
        return id;
    }

    public ReWr getPermission() {
        return permission;
    }

    private void setPermission(ReWr permission) {
        this.permission = permission;
    }

    public String getAuthor() {
        return author;
    }

    public ArrayList<String> getAttribute() {
        return attribute;
    }

    /**
     * 对外开放的权限求改方法，需要密码
     */
    public boolean changePermission (String password, ReWr permission) {
        if (this.password.equals(password)) {
            setPermission(permission);
            return true;
        }
        return false;
    }

    /**
     * 对外开放的修改密码方法，需要原密码
     */
    public boolean changePassword (String password, String newPassword) {
        if (this.password.equals(password)) {
            this.password = newPassword;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("<%s, %d, %s>", this.getName(), this.getId(), this.permission);
    }

    /**
     * 只有当名字和 id 都相同的 Flag 才是相同的。
     *
     * @param obj -other object to check equality
     * @return true if both id and name are the same.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Flag)) {
            return false;
        } else {
            Flag otherFlag = (Flag) obj;
            return otherFlag.getName().equals(this.getName())
                    && otherFlag.getId() == this.getId();
        }
    }
}
