package com.example.MultiTenent.Services;

import com.example.MultiTenent.Models.Model1;
import com.example.MultiTenent.Repositorys.db1Repository.db1Repository;
import com.example.MultiTenent.Repositorys.db2Repository.db2Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class userService {

    @Autowired
    private db1Repository db1;
    @Autowired
    private db2Repository db2;
    public ResponseEntity<?> createUser(int DBno , Model1 obj){
        Model1 obj1;
        try {

            if (DBno == 0) {
                obj.setDataBase("Main-DataBase");
                obj1 = db1.save(obj);
                obj.setDataBase("Backup-DataBase");
                db2.save(obj);
            } else {
                obj.setDataBase("Main-DataBase");
                obj1 = db1.save(obj);
            }
        }catch (org.springframework.dao.DuplicateKeyException ex){
            return ResponseEntity.status(HttpStatus.OK).body("Please provide unique userName");
        }
        return ResponseEntity.status(HttpStatus.OK).body(obj1);
    }

    public ResponseEntity<?> getUsers(int DBno){
        List<Model1> obj1=null;
        if(DBno==1){
            obj1= db1.findAll();
        }else if(DBno==2){
            obj1= db2.findAll();
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You have given wrong DB number , please give from 1 and 2.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(obj1);
    }

    public ResponseEntity<Model1> update( String userName){
        List<Model1> userOptional = db1.findByUserName(userName);
        String fullName = userOptional.get(0).getFullName();
        String newStr = "";
        String allSpecialChar="<>@!#$%^&*()_+[]{}?:|'/~`-=";
        int low = 0;
        int high = 26;
        for(int i=0;i<fullName.length();i++){
            char ele=fullName.charAt(i);
            Integer code=(int) ele;
            if(ele=='a'||ele=='e'||ele=='i'||ele=='o'||ele=='u'||ele=='A'||ele=='E'||ele=='I'||ele=='O'||ele=='U'){
                Random r = new Random();
                int randomNo = r.nextInt(high-low) + low;
                newStr=newStr+allSpecialChar.charAt(randomNo);
            }else{
                newStr=newStr+ele;
            }
        }
        userOptional.get(0).setFullName(newStr);
        Model1 savedData=db1.save(userOptional.get(0));
        return ResponseEntity.status(HttpStatus.OK).body(savedData);
    }

    public ResponseEntity<String> deleteByEmpId( String userName){
        db1.deleteByUserName(userName);
        return ResponseEntity.status(HttpStatus.OK).body("Deleted Successfully.");
    }
}
