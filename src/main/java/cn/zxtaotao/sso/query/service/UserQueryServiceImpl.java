package cn.zxtaotao.sso.query.service;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.zxtaotao.common.service.RedisService;
import cn.zxtaotao.sso.query.api.UserQueryService;
import cn.zxtaotao.sso.query.bean.User;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserQueryServiceImpl implements UserQueryService {

    @Autowired
    private RedisService redisService;

    private static final String REDIS_KEY = "TOKEN_";

    private static final Integer REDIS_TIME = 60 * 30;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public User queryUserByToken(String token) {
        String key = REDIS_KEY + token;
        try {
            String jsonData = this.redisService.get(key);
            if (StringUtils.isEmpty(jsonData)) {
                // 登录超时
                return null;
            }
            // 重新刷新用户的生存时间
            this.redisService.expire(key, REDIS_TIME);
            return MAPPER.readValue(jsonData, User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
