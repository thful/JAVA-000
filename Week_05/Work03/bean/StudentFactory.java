package bean;

public class StudentFactory  {
    public  Student create(){
        Student st=new Student();
        st.setName("工厂");
        st.setGender("男");
        return st;
    }
}