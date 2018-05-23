/*
 * Copyright (C) 2017 B3Partners B.V.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package nl.b3p.playbase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 *
 * @author Meine Toonen
 */
public class ImageDownloader {

    private final Log log = LogFactory.getLog(this.getClass());

    private final List<ImageCollector> ics = new ArrayList<>();
    private final String downloadPath;

    private final int MAX_THREADS = 8;
    private ExecutorService threadPool = null;
    private CompletionService<ImageCollector> pool = null;
    private CloseableHttpClient client = null;
    private final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();

    private boolean running = false;

    public ImageDownloader(String path) {
        downloadPath = path;
        if (path != null) {
            threadPool = Executors.newFixedThreadPool(MAX_THREADS);
            pool = new ExecutorCompletionService<>(threadPool);

            client = HttpClients.custom()
                    .setConnectionManager(cm)
                    .build();
        }
    }

    public void add(String url, String filename) {
        if (downloadPath != null) {
            ImageCollector ic = new ImageCollector(url, filename, client);
            if (running) {
                pool.submit(ic);
            } else {
                ics.add(ic);
            }
        }
    }

    public void run() {
        if (downloadPath != null) {
            running = true;
            for (ImageCollector ic : ics) {
                pool.submit(ic);
            }
            //wait for all to complete. Wait max 5 min
            /* for (int i = 0; i < ics.size(); i++) {
            pool.poll(5, TimeUnit.MINUTES).get();            
        } */
        }
    }

    public void stop() throws IOException {

        if (downloadPath != null) {
            try {
                running = false;
                threadPool.awaitTermination(1, TimeUnit.MINUTES);
                client.close();
            } catch (InterruptedException ex) {
                log.error("Error terminating threadpool", ex);
                client.close();
            }
        }
    }

    class ImageCollector implements Callable {

        public static final int NEW = 0;
        public static final int ACTIVE = 1;
        public static final int COMPLETED = 2;
        public static final int WARNING = 3;
        public static final int ERROR = 4;

        private final CloseableHttpClient client;

        private final String url;

        private String message;
        private int status;
        private File file;

        public ImageCollector(String url, String filename, CloseableHttpClient client) {
            this.url = url;
            this.client = client;
            file = new File(downloadPath, filename);
        }

        @Override
        public Object call() throws Exception {
            status = ACTIVE;
            if (url == null || url.length() == 0 ) {
                return this;
            }

            if(file.exists()){
                return this;
            }
            
            try {
                loadImage(url);
                setMessage("");
                setStatus(COMPLETED);
            } catch (Exception ex) {
                log.warn("error callimage collector: ", ex);
                setStatus(ERROR);
            }
            return this;
        }

        protected void loadImage(String url) throws IOException, Exception {

            try {
                HttpGet httpget = new HttpGet(url);
                try (CloseableHttpResponse response = client.execute(httpget, new BasicHttpContext())) {
                    // get the response body as an array of bytes
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        byte[] bytes = EntityUtils.toByteArray(entity);
                        try (FileOutputStream fos = new FileOutputStream(file)) {
                            fos.write(bytes);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Cannot download image from: " + url, e);
            }
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

    }
}
