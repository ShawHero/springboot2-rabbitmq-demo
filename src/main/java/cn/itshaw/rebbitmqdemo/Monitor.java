package cn.itshaw.rebbitmqdemo;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.http.HttpEntity;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class Monitor {

    public static void main(String[] args) {
        try {
            Map<String, ClusterStatus> map = fetchNodesStatusData("http://192.168.235.108:15672/api/nodes", "admin", "admin");
            Map<String, VhostsStatus> vmap = fetchVhostsStatusData("http://192.168.235.108:15672/api/vhosts", "admin", "admin");
            for (Map.Entry entry : map.entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }
            for (Map.Entry entry : vmap.entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String,VhostsStatus> fetchVhostsStatusData(String url, String username, String password) throws IOException {
        Map<String, VhostsStatus> clusterStatusMap = new HashMap<String, VhostsStatus>();
        String nodeData = getData(url, username, password);
        JsonNode jsonNode = null;
        try {
            jsonNode = JsonUtil.toJsonNode(nodeData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Iterator<JsonNode> iterator = jsonNode.iterator();
        while (iterator.hasNext()) {
            JsonNode next = iterator.next();
            VhostsStatus status = new VhostsStatus();
            status.setMessages(next.get("messages").asLong());
            status.setMessagesReady(next.get("messages_ready").asLong());
            status.setMessagesUnacked(next.get("messages_unacknowledged").asLong());
            if(next.get("send_oct_details")!=null){
                status.setPublishRate(next.get("send_oct_details").get("rate").asLong());
            }
            if(next.get("recv_oct_details")!=null) {
                status.setDeliverRate(next.get("recv_oct_details").get("rate").asLong());
            }
            clusterStatusMap.put(next.get("name").asText(), status);
        }
        return clusterStatusMap;
    }

    public static Map<String,ClusterStatus> fetchNodesStatusData(String url, String username, String password) throws IOException {
        Map<String, ClusterStatus> clusterStatusMap = new HashMap<String, ClusterStatus>();
        String nodeData = getData(url, username, password);
        JsonNode jsonNode = null;
        try {
            jsonNode = JsonUtil.toJsonNode(nodeData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Iterator<JsonNode> iterator = jsonNode.iterator();
        while (iterator.hasNext()) {
            JsonNode next = iterator.next();
            ClusterStatus status = new ClusterStatus();
            status.setDiskFree(next.get("disk_free").asLong());
            status.setDiskLimit(next.get("disk_free_limit").asLong());
            status.setFdUsed(next.get("fd_used").asLong());
            status.setFdTotal(next.get("fd_total").asLong());
            status.setMemoryUsed(next.get("mem_used").asLong());
            status.setMemoryLimit(next.get("mem_limit").asLong());
            status.setProcUsed(next.get("proc_used").asLong());
            status.setProcTotal(next.get("proc_total").asLong());
            status.setSocketUsed(next.get("sockets_used").asLong());
            status.setSocketTotal(next.get("sockets_total").asLong());
            clusterStatusMap.put(next.get("name").asText(), status);
        }
        return clusterStatusMap;
    }

    public static String getData(String url, String username, String password) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader(BasicScheme.authenticate(creds, "UTF-8", false));
        httpGet.setHeader("Content-Type", "application/json");
        CloseableHttpResponse response = httpClient.execute(httpGet);

        try {
            if (response.getStatusLine().getStatusCode() != 200) {
                System.out.println("call http api to get rabbitmq data return code: " + response.getStatusLine().getStatusCode() + ", url: " + url);
            }
            HttpEntity entity = (HttpEntity) response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity);
            }
        } finally {
            response.close();
        }

        return "";
    }

    public static class JsonUtil {
        private static ObjectMapper objectMapper = new ObjectMapper();
        static {
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        }

        public static JsonNode toJsonNode(String jsonString) throws IOException {
            return objectMapper.readTree(jsonString);
        }
    }
    public static class VhostsStatus {
        private long messages;
        private long messagesUnacked;
        private long messagesReady;
        private long publishRate;
        private long deliverRate;

        @Override
        public String toString() {
            return "VhostsStatus{" +
                    "messages=" + getMessages() +
                    ", messagesUnacked=" + getMessagesUnacked() +
                    ", messagesReady=" + getMessagesReady() +
                    ", publishRate=" + getPublishRate() +
                    ", deliverRate=" + getDeliverRate() +
                    '}';
        }

        public long getMessages() {
            return messages;
        }

        public void setMessages(long messages) {
            this.messages = messages;
        }

        public long getMessagesUnacked() {
            return messagesUnacked;
        }

        public void setMessagesUnacked(long messagesUnacked) {
            this.messagesUnacked = messagesUnacked;
        }

        public long getMessagesReady() {
            return messagesReady;
        }

        public void setMessagesReady(long messagesReady) {
            this.messagesReady = messagesReady;
        }


        public long getPublishRate() {
            return publishRate;
        }

        public void setPublishRate(long publishRate) {
            this.publishRate = publishRate;
        }

        public long getDeliverRate() {
            return deliverRate;
        }

        public void setDeliverRate(long deliverRate) {
            this.deliverRate = deliverRate;
        }
    }

    public static class ClusterStatus {
        private long diskFree;
        private long diskLimit;
        private long fdUsed;
        private long fdTotal;
        private long socketUsed;
        private long socketTotal;
        private long memoryUsed;
        private long memoryLimit;
        private long procUsed;
        private long procTotal;

        @Override
        public String toString() {
            return "ClusterStatus{" +
                    "diskFree=" + getDiskFree() +
                    ", diskLimit=" + getDiskLimit() +
                    ", fdUsed=" + getFdUsed() +
                    ", fdTotal=" + getFdTotal() +
                    ", socketUsed=" + getSocketUsed() +
                    ", socketTotal=" + getSocketTotal() +
                    ", memoryUsed=" + getMemoryUsed() +
                    ", memoryLimit=" + getMemoryLimit() +
                    ", procUsed=" + getProcUsed() +
                    ", procTotal=" + getProcTotal() +
                    '}';
        }

        public long getDiskFree() {
            return diskFree;
        }

        public void setDiskFree(long diskFree) {
            this.diskFree = diskFree;
        }

        public long getDiskLimit() {
            return diskLimit;
        }

        public void setDiskLimit(long diskLimit) {
            this.diskLimit = diskLimit;
        }

        public long getFdUsed() {
            return fdUsed;
        }

        public void setFdUsed(long fdUsed) {
            this.fdUsed = fdUsed;
        }

        public long getFdTotal() {
            return fdTotal;
        }

        public void setFdTotal(long fdTotal) {
            this.fdTotal = fdTotal;
        }

        public long getSocketUsed() {
            return socketUsed;
        }

        public void setSocketUsed(long socketUsed) {
            this.socketUsed = socketUsed;
        }

        public long getSocketTotal() {
            return socketTotal;
        }

        public void setSocketTotal(long socketTotal) {
            this.socketTotal = socketTotal;
        }

        public long getMemoryUsed() {
            return memoryUsed;
        }

        public void setMemoryUsed(long memoryUsed) {
            this.memoryUsed = memoryUsed;
        }

        public long getMemoryLimit() {
            return memoryLimit;
        }

        public void setMemoryLimit(long memoryLimit) {
            this.memoryLimit = memoryLimit;
        }

        public long getProcUsed() {
            return procUsed;
        }

        public void setProcUsed(long procUsed) {
            this.procUsed = procUsed;
        }

        public long getProcTotal() {
            return procTotal;
        }

        public void setProcTotal(long procTotal) {
            this.procTotal = procTotal;
        }
    }
}
