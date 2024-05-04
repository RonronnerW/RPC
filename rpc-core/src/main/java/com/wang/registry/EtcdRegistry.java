package com.wang.registry;

import cn.hutool.json.JSONUtil;
import com.wang.config.RegistryConfig;
import com.wang.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author wanglibin
 * @version 1.0
 */
@Slf4j
public class EtcdRegistry implements Registry{

    private Client client;

    private KV kvClient;

    private static final String ETCD_ROOT_PATH = "/rpc/";

    @Override
    public void init(RegistryConfig registryConfig) {
        // create client using endpoints
        client = Client.builder()
                .endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        // 创建 lease 和 KV 客户端
        Lease leaseClient = client.getLeaseClient();

        //创建30s租约
        long leaseId = leaseClient.grant(30).get().getID();

        String registerKey = ETCD_ROOT_PATH+serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        // 将键值对和租约关联起来
        PutOption putOption = PutOption.newBuilder().withLeaseId(leaseId).build();
        // put the key-value
        kvClient.put(key, value, putOption).get();
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        String registerKey = ETCD_ROOT_PATH+serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        kvClient.delete(key);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // 前缀搜索结尾一定要加 /
        String prefix = ETCD_ROOT_PATH+serviceKey+"/";

        try {
            // 前缀查询
            GetOption getOption = GetOption.newBuilder().isPrefix(true).build();
            List<KeyValue> kvs = kvClient.get(ByteSequence.from(prefix, StandardCharsets.UTF_8), getOption)
                    .get()
                    .getKvs();
            // 解析服务信息
            return kvs.stream()
                    .map(keyValue -> {
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void heartBeat() {

    }

    @Override
    public void watch(String serviceNodeKey) {

    }

    /**
     * 注册中心销毁，用于项目关闭后释放资源
     */
    @Override
    public void destroy() {
        log.info("当前节点下线");
        if(kvClient != null) {
            kvClient.close();
        }
        if(client!=null) {
            client.close();
        }
    }
}
