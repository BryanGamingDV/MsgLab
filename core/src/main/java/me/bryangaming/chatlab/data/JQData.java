package me.bryangaming.chatlab.data;

import java.util.List;

public class JQData {

    private int firstJoinNextId = 0;
    private int joinNextId = 0;
    private int quitNextId = 0;

    private String firstJoinType;
    private String joinType;
    private String quitType;

    private List<String> firstJoinFormat;
    private List<String> joinFormat;
    private List<String> quitFormat;

    private boolean firstJoinHook;
    private boolean joinHook;

    private List<String> firstJoinMotdList;
    private List<String> joinMotdList;

    private List<String> firstJoinActions;
    private List<String> joinActions;
    private List<String> quitActions;

    private String name;

    public JQData(String name) {
        this.name = name;
    }

    public int getFirstJoinNextId() {
        return firstJoinNextId;
    }

    public boolean firstJoinIdIsTheMax(int id) {
        return firstJoinNextId + 1 == id;
    }

    public void sumFirstJoinNextId(){
        this.firstJoinNextId++;
    }

    public void clearFirstJoinNextId(){
        this.firstJoinNextId = 0;
    }


    public boolean getFirstJoinHook(){
        return firstJoinHook;
    }

    public void setFirstJoinHook(boolean hook){
        this.firstJoinHook = hook;
    }

    public int getJoinNextId() {
        return joinNextId;
    }

    public boolean joinIdIsTheMax(int id) {
        return joinNextId + 1 == id;
    }

    public void sumJoinNextId() {
        this.joinNextId++;
    }

    public void clearJoinNextId() {
        joinNextId = 0;
    }

    public boolean getJoinHook(){
        return joinHook;
    }

    public void setJoinHook(boolean hook){
        this.joinHook = hook;
    }

    public int getQuitNextId() {
        return quitNextId;
    }

    public boolean quitIdIsTheMax(int id) {
        return quitNextId + 1 == id;
    }

    public void sumQuitNextId() {
        this.quitNextId++;
    }

    public void clearQuitNextId() {
        this.quitNextId = 0;
    }

    public String getFirstJoinType() {
        return firstJoinType;
    }

    public void setFirstJoinType(String firstJoinType) {
        this.firstJoinType = firstJoinType;
    }

    public String getJoinType() {
        return joinType;
    }

    public void setJoinType(String joinType) {
        this.joinType = joinType;
    }

    public String getQuitType() {
        return quitType;
    }

    public void setQuitType(String quitType) {
        this.quitType = quitType;
    }

    public List<String> getFirstJoinFormat() {
        return firstJoinFormat;
    }

    public void setFirstJoinFormat(List<String> firstJoinFormat) {
        this.firstJoinFormat = firstJoinFormat;
    }

    public List<String> getJoinFormat() {
        return joinFormat;
    }

    public void setJoinFormat(List<String> joinFormat) {
        this.joinFormat = joinFormat;
    }

    public List<String> getQuitFormat() {
        return quitFormat;
    }

    public void setQuitFormat(List<String> quitFormat) {
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

    public List<String> getFirstJoinActions() {
        return firstJoinActions;
    }

    public void setFirstJoinActions(List<String> firstJoinActions) {
        this.firstJoinActions = firstJoinActions;
    }

    public List<String> getJoinActions() {
        return joinActions;
    }

    public void setJoinActions(List<String> joinActions) {
        this.joinActions = joinActions;
    }

    public List<String> getQuitActions() {
        return quitActions;
    }

    public void setQuitActions(List<String> quitActions) {
        this.quitActions = quitActions;
    }
}
