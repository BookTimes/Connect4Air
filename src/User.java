
public class User {
    private String name ;
    private String pass;
    public static int userId;
    User(String name , String pass){
        this.name = name;
        this.pass = pass;
    }

    public boolean validateUser(){
        User.userId =  databaseHandler.login(this.name , this.pass);
        return (userId == 1)? true : false;
    }
    
}


