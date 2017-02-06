package com.example.egguncle.chattest.model;



/**
 * Created by egguncle on 17-1-21.
 * 用于用户登录的json
 */
public class LoginJson {

    //是否发生错误
    private boolean error;

    //结果
    private UserEntity userEntity;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }
}
