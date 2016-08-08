package hu.poketerkep.client.model;


import hu.poketerkep.client.model.helpers.LastUsed;

@SuppressWarnings("unused")
public class UserConfig implements LastUsed {
    private String userName;
    private Long lastUsed;
    private Boolean banned;

    public UserConfig(String userName, Long lastUsed, Boolean banned) {
        this.userName = userName;
        this.lastUsed = lastUsed;
        this.banned = banned;
    }

    public UserConfig() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Long lastUsed) {
        this.lastUsed = lastUsed;
    }

    public Boolean getBanned() {
        return banned;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }

    @Override
    public String toString() {
        return "UserConfig{" +
                "userName='" + userName + '\'' +
                ", lastUsed=" + lastUsed +
                ", banned=" + banned +
                '}';
    }
}
