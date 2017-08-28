package tech.xpercent.springrpc.server;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import tech.xpercent.springrpc.common.Constant;

import java.util.concurrent.CountDownLatch;

/**
 * Created by macxie on 8/4/17.
 */
@Slf4j
public class ServiceRegistry {

    private CountDownLatch latch = new CountDownLatch(1);
    private String registryAddress;

    public ServiceRegistry() {
    }

    public ServiceRegistry(String registryAddress) {
        //zookeeper的地址
        this.registryAddress = registryAddress;
    }

    /**
     * 创建zookeeper链接
     *
     * @param data
     */
    public void regist(String data) {
        if (data != null) {
            ZooKeeper zk = connectZK();
            if (zk != null) {
                createNode(zk, data);
            }
        }
    }

    /**
     * 创建节点
     *
     * @param zk
     * @param data
     */
    private void createNode(ZooKeeper zk, String data) {
        try {
            byte[] bytes = data.getBytes();
            if (zk.exists(Constant.ZK_REGISTRY_PATH, null) == null) {
                zk.create(Constant.ZK_REGISTRY_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            }
            String path = zk.create(Constant.ZK_DATA_PATH, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            log.debug("create zookeeper node ({} => {})", path, data);
        } catch (Exception e) {
            log.error("create zookeeper node failed", e);
        }
    }

    /**
     * 创建zookeeper链接，监听
     *
     * @return
     */
    private ZooKeeper connectZK() {
        ZooKeeper zk = null;
        try {
            new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher() {
                public void process(WatchedEvent watchedEvent) {
                    if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                        latch.countDown();
                    }
                }
            });
            latch.await();
        } catch (Exception e) {
            log.error("zk connected fail, messages:s", e);
        }
        return zk;
    }
}
