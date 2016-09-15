package com.jk.examenma15;

/**
 * Created by jk on 29/08/16.
 */
public class ToDo {

    private String text;
    private Integer expiredate; // Should be changed to a date object

    public ToDo(){
    }

    public ToDo(String text, Integer expiredate){
        this.text = text;
        this.expiredate = expiredate;
    }

    public Integer getExpiredate() {
        return expiredate;
    }

    public String getText() {return text; }

    @Override
    public String toString() {
        return "ToDo{" +
                "text='" + text + '\'' +
                ", expiredate=" + expiredate +
                '}';
    }
}
