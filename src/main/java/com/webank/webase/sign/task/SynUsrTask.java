package com.webank.webase.sign.task;


import com.webank.webase.sign.api.service.UserService;
import com.webank.webase.sign.pojo.po.UserInfoPo;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SynUsrTask {


    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private UserService userService;

    public static LocalDateTime synTime;

    @Scheduled(fixedDelayString = "${constant.syncUsrCacheTaskFixedDelay}")
    public void taskStart() {
        syncUsrCacheTask();
    }

    public synchronized void syncUsrCacheTask() {
        log.debug("start syncUsrCacheTask task");

        UserInfoPo user = userService.findLatestUpdatedUser();
        if (user == null) {
            return;
        }

        log.debug("latest delete userId :" + user.getSignUserId());
        LocalDateTime dbLatestUpdateTime = user.getGmtModify();

        log.debug("***" + synTime + "****db : " + dbLatestUpdateTime);
        if (synTime != null && synTime.isBefore(dbLatestUpdateTime)) {

            List<UserInfoPo> userInfoPoList =
                    userService.findUserListByTime(synTime, dbLatestUpdateTime);
            Cache cache = cacheManager.getCache("user");
            for (int i = 0; i < userInfoPoList.size(); i++) {
                UserInfoPo userInfoPo = userInfoPoList.get(i);
                if (cache.get(userInfoPo.getSignUserId()) != null) {
                    cache.evict(userInfoPo.getSignUserId());
                    log.debug("evict  : {}", userInfoPo.getSignUserId());
                }
            }
        }
        synTime = dbLatestUpdateTime;


        log.debug("end syncUsrCacheTask task");

    }
}
