package me.bryangaming.chatlab.data;

import java.util.List;

public class JQData {

    private List<String> firstJoinCommands;
    private List<String> joinCommands;
    private List<String> quitCommands;

    private String firstJoinFormat;
    private String joinFormat;
    private String quitFormat;

    private List<String> firstJoinMotdList;
    private List<String> joinMotdList;

    private String name;

    public JQData(String name) {
        this.name = name;
    }

    public List<String> getFirstJoinCommands() {
        return firstJoinCommands;
    }

    public void setFirstJoinCommands(List<String> firstJoinCommands) {
        this.firstJoinCommands = firstJoinCommands;
    }

    public List<String> getJoinCommands() {
        return joinCommands;
    }

    public void setJoinCommands(List<String> joinCommands) {
        this.joinCommands = joinCommands;
    }

    public List<String> getQuitCommands() {
        return quitCommands;
    }

    public void setQuitCommands(List<String> quitCommands) {
        this.quitCommands = quitCommands;
    }

    public String getFirstJoinFormat() {
        return firstJoinFormat;
    }

    public void setFirstJoinFormat(String firstJoinFormat) {
        this.firstJoinFormat = firstJoinFormat;
    }

    public String getJoinFormat() {
        return joinFormat;
    }

    public void setJoinFormat(String joinFormat) {
        this.joinFormat = joinFormat;
    }

    public String getQuitFormat() {
        return quitFormat;
    }

    public void setQuitFormat(String quitFormat) {
        this.quitFormat = quitFormat;
    }

    public List<String> getFirstJoinMotdList() {
        return firstJoinMotdList;
    }

    public void setFirstJoinMotdList(List<String> firstJoinMotdList) {
        this.firstJoinMotdList = firstJoinMotdList;
    }

    public List<String> getJoinMotdList() {
        return joinMotdList;
    }

    public void setJoinMotdList(List<String> joinMotdList) {
        this.joinMotdList = joinMotdList;
    }
}
