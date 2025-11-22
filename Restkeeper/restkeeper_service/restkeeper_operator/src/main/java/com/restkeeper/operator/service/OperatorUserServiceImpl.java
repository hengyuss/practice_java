package com.restkeeper.operator.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.operator.entity.OperatorUser;
import com.restkeeper.operator.mapper.OperatorUserMapper;
import com.restkeeper.utils.JWTUtil;
import com.restkeeper.utils.MD5CryptUtil;
import com.restkeeper.utils.Result;
import io.netty.util.internal.StringUtil;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//@Service("operatorUserService")
@Service(version = "1.0.0",protocol = "dubbo")
/**
 * dubbo中支持的协议
 * dubbo 默认
 * rmi
 * hessian
 * http
 * webservice
 * thrift
 * memcached
 * redis
 */

@RefreshScope
public class OperatorUserServiceImpl extends ServiceImpl<OperatorUserMapper, OperatorUser> implements IOperatorUserService{

    @Value("${gateway.secret}")
    private String secret;

    @Override
    public Result login(String username, String password) {
        Result result = new Result();
        if(StringUtil.isNullOrEmpty(username) || StringUtil.isNullOrEmpty(password)){
            result.error("用户名或密码错误");
        }
        QueryWrapper<OperatorUser> queryWrapper = new QueryWrapper<>();
        OperatorUser queryUser = this.getOne(queryWrapper);
        if(queryUser == null){
            result.error("用户不存在");
        }
        String queryPassword = queryUser.getLoginpass();
        String salts = MD5CryptUtil.getSalts(queryPassword);
        if (!StringUtils.equals(Md5Crypt.md5Crypt(password.getBytes(), salts), queryPassword)){
            result.error("用户名或密码错误");
        }

        Map<String, Object> tokeInfo = new HashMap<>();
        tokeInfo.put("username", username);
        String token;
        try {
            token = JWTUtil.createJWTByObj(tokeInfo, secret);
        } catch (IOException e) {
            e.printStackTrace();
            result.error("令牌生成失败");
            return result;
        }
        result.setToken(token);
        result.ok(queryUser);

        return result;
    }
}
