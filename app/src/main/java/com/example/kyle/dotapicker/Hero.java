package com.example.kyle.dotapicker;

/**
 * Created by Kyle on 2/15/2017.
 */

public class Hero {
    private double total;
    private double ally;
    private double enemy;
    private String thumbnailUrl;
    private String name;
    private int icon;
    private String subnames;
    private int id;
    private double adv;
    private double synergy;
    private double bayesWR;
    private double bayesLR;

    public Hero(){
    }

    public Hero(String name, String thumbnailUrl, double total){
        this.name = name;
        this.thumbnailUrl = thumbnailUrl;
        this.total = total;
    }

    public Hero(String name, int icon, int id, Double total, Double ally, Double enemy, Double adv, Double syn, Double bayesWR, Double bayesLR) {
        this.name = name;
        this.icon = icon;
        this.id = id;
        this.total = total;
        this.ally = ally;
        this.enemy = enemy;
        this.adv = adv;
        this.synergy = syn;
        this.bayesWR = bayesWR;
        this.bayesLR = bayesLR;
    }

    public Hero(String name, int image, int id, String subnames) {
        this.name = name;
        this.icon = image;
        this.id = id;
        this.subnames = subnames;
    }

    public String getSubnames() {
        return subnames;
    }

    public int getId() {
        return id;
    }

    public Double getAdvantage() {
        return adv;
    }

    public Double getSynergy() {return synergy; }


    public void setSubnames(String subname) {
        this.subnames = subnames;
    }

    public double getTotal() {
        return total;
    }

    public int getIcon() {
        return icon;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getAlly() {
        return ally;
    }

    public void setAlly(double ally) {
        this.ally = ally;
    }

    public double getEnemy() {
        return enemy;
    }

    public void setEnemy(double enemy) {
        this.enemy = enemy;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBayesAlliedWR(){
        return bayesWR;
    }

    public double getBayesEnemyWR(){
        return bayesLR;
    }

    @Override
    public boolean equals(Object anObject) {
        if (!(anObject instanceof Hero)) {
            return false;
        }
        Hero otherMember = (Hero)anObject;
        return otherMember.getId() == getId();
    }
}
