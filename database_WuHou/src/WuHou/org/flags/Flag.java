package WuHou.org.flags;

import java.security.KeyPair;

public class Flag {
    /** flag 的名字 */
    private String flag;
    /** flag 的编号 */
    private int id;

    public Flag(String flag) {
        this.flag = flag;
        this.id = 0;
    }

    public String getName() {
        return flag;
    }

    public void setName(String newFlag) {
        this.flag = newFlag;
    }

    public int getId() {
        return id;
    }

    public int updateId() {
        this.id = this.id + 1;
        return id;
    }

    @Override
    public String toString() {
        return String.format("<%s, %d>", this.getName(), this.getId());
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
