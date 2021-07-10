package me.bryangaming.chatlab.data;

import org.minidns.record.A;
import redis.clients.jedis.StreamGroupInfo;

import java.util.List;

public class JQData {

    private int authJoinNextId = 0;
    private int firstJoinNextId = 0;
    private int joinNextId = 0;
    private int quitNextId = 0;

    private String authType;
    private String firstJoinType;
    private String joinType;
    private String quitType;

    private List<String> authFormat;
    private List<String> firstJoinFormat;
    private List<String> joinFormat;
    private List<String> quitFormat;

    private List<String> authMotdList;
    private List<String> firstJoinMotdList;
    private List<String> joinMotdList;

    private List<String> authActions;
    private List<String> firstJoinActions;
    private List<String> joinActions;
    private List<String> quitActions;

    private String name;

    public JQData(String name) {
        this.name = name;
    }

    // ID
    public int getAuthNextId() {
        return authJoinNextId;
    }

    public boolean authIdIsTheMax(int id) {
        return authJoinNextId + 1 == id;
    }

    public void sumAuthNextId(){
        this.authJoinNextId++;
    }

    public void clearAuthNextId(){
        this.authJoinNextId = 0;
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

    // Type:
    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
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

    // Format:
    public List<String> getAuthFormat(){
        return authFormat;
    }

    public void setAuthFormat(List<String> authFormat){
        this.authFormat = authFormat;
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


    // Motd:
    public List<String> getAuthMotdList(){
        return authMotdList;
    }

    public void setAuthMotdList(List<String> authMotdList){
        this.authMotdList = authMotdList;
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

    // Actions:
    public List<String> getAuthActions(){
        return authActions;
    }

    public void setAuthActions(List<String> actions){
        this.authActions = actions;
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
