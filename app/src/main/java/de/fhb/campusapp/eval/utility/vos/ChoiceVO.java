package de.fhb.campusapp.eval.utility.vos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Admin on 15.05.2016.
 */
public class ChoiceVO implements Serializable{

    private String choiceText;
    private short grade;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeUTF(choiceText);
        out.writeInt(grade);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        choiceText = in.readUTF();
        grade = (short) in.readInt();
    }

    public ChoiceVO() {    }

    public ChoiceVO(String choiceText, short grade) {
        this.choiceText = choiceText;
        this.grade = grade;
    }

    public String getChoiceText() {
        return choiceText;
    }

    public void setChoiceText(String choiceText) {
        this.choiceText = choiceText;
    }

    public short getGrade() {
        return grade;
    }

    public void setGrade(short grade) {
        this.grade = grade;
    }
}
