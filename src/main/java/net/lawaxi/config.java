package net.lawaxi;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.Setting;
import net.lawaxi.models.User;
import net.lawaxi.models.UserWives;
import net.mamoe.mirai.console.plugin.PluginManager;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class config {

    private final File config;
    private final Setting s;
    private List<User> users = new ArrayList<>();
    private List<UserWives> wives = new ArrayList<>();

    public config(File config) {
        this.config = config;
        this.s = new Setting(config, StandardCharsets.UTF_8, false);

        if (!config.exists()) {
            FileUtil.touch(config);

            s.setByGroup("users", "users", "[]");
            s.setByGroup("wives", "wives", "[]");

            s.store();
        }
        load();
    }

    private void load(){

        for (Object o : JSONUtil.parseArray(s.getByGroup("users", "users")).toArray()) {
            JSONObject o1 = JSONUtil.parseObj(o);
            users.add(new User(o1.getLong("g"),o1.getLong("m")));
        }

        for (Object o : JSONUtil.parseArray(s.getByGroup("wives", "wives")).toArray()) {
            JSONObject o1 = JSONUtil.parseObj(o);
            UserWives w = new UserWives();
            wives.add(w);
            for(String key : o1.keySet()){
                w.put(Long.getLong(key), o1.getInt(key));
            }
        }
    }

    private void save(){
        s.setByGroup("users","users", listToJson(users));
        s.setByGroup("wives","wives", listToJson(wives));
        s.store();
    }

    private String listToJson(List l){
        String out = "[";
        for(Object o : l){
            out+=o.toString()+",";
        }
        if(out.length()>1)
            return out.substring(0,out.length()-1)+"]";
        return "[]";
    }

    public int getUserIndex(long group, long member){
        for(int i = 0;i<this.users.size();i++){
            User user = this.users.get(i);
            if(user.g == group && user.m == member)
                return i;
        }
        return -1;
    }

    public void addNewWive(long group, long member, Long wife){
        if(getUserIndex(group,member) == -1){
            users.add(new User(group,member));
            wives.add(new UserWives());
        }

        wives.get(getUserIndex(group,member)).add(wife);
        save();
    }

    public UserWives getUserWives(long group, long member){
        if(getUserIndex(group,member) == -1){
            users.add(new User(group,member));
            wives.add(new UserWives());
            save();
        }

        return wives.get(getUserIndex(group,member));
    }
}
