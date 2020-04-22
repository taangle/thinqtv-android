package com.thinqtv.thinqtv_android.data;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Custom Volley request to send file.
 */
public class VolleyMultipartRequest extends Request<NetworkResponse> {
    private final String hyphens = "--";
    private final String endLine = "\r\n";
    private final String boundary = "apiclient-" + System.currentTimeMillis();

    private Response.Listener<NetworkResponse> listener;
    private Response.ErrorListener errorListener;
    private Map<String, String> requestHeaders;

    public VolleyMultipartRequest(String url, Map<String, String> requestHeaders,
                                  Response.Listener<NetworkResponse> listener,
                                  Response.ErrorListener errorListener) {
        super(Request.Method.POST, url, errorListener);
        this.listener = listener;
        this.errorListener = errorListener;
        this.requestHeaders = requestHeaders;
    }

    public VolleyMultipartRequest(int method, String url, Response.Listener<NetworkResponse> listener,
                                  Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = listener;
        this.errorListener = errorListener;
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (requestHeaders != null) {
            return requestHeaders;
        }
        return super.getHeaders();
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + boundary;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);

        try {
            Map<String, String> params = getParams();
            if (params != null && params.size() > 0) {
                textParse(dataStream, params, getParamsEncoding());
            }

            Map<String, DataPart> data = getByteData();
            if (data != null && data.size() > 0) {
                dataParse(dataStream, data);
            }

            dataStream.writeBytes(hyphens + boundary + hyphens + endLine);
            return byteStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    protected Map<String, DataPart> getByteData() throws AuthFailureError {
        return null;
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
        } catch(Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        listener.onResponse(response);
    }

    private void textParse(DataOutputStream dataStream, Map<String, String> params, String encoding) throws IOException {
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                buildTextPart(dataStream, entry.getKey(), entry.getValue());
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding not supported: " + encoding, e);
        }
    }

    private void dataParse(DataOutputStream dataStream, Map<String, DataPart> data) throws IOException {
        for (Map.Entry<String, DataPart> entry : data.entrySet()) {
            buildDataPart(dataStream, entry.getKey(), entry.getValue());
        }
    }

    private void buildTextPart(DataOutputStream dataStream, String paramKey, String paramValue) throws IOException {
        dataStream.writeBytes(hyphens + boundary + endLine);
        dataStream.writeBytes("Content-Disposition: form-data; name=\"" + paramKey + "\"" + endLine);
        dataStream.writeBytes(endLine);
        dataStream.writeBytes(paramValue + endLine);
    }

    private void buildDataPart(DataOutputStream dataStream, String name, DataPart file) throws IOException {
        dataStream.writeBytes(hyphens + boundary + endLine);
        dataStream.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" +
                file.getFileName() + "\"" + endLine);
        if (file.getType() != null && !file.getType().trim().isEmpty()) {
            dataStream.writeBytes("Content-Type: " + file.getType() + endLine);
        }
        dataStream.writeBytes(endLine);

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(file.getContent());
        int bytesAvailable = fileInputStream.available();
        int maxBufferSize = 1024 * 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            dataStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        dataStream.writeBytes(endLine);
    }


    public class DataPart {
        private String fileName;
        private byte[] content;
        private String type;

        public DataPart() {

        }

        public DataPart(String fileName, byte[] content) {
            this.fileName = fileName;
            this.content = content;
        }

        public DataPart(String fileName, byte[] content, String type) {
            this.fileName = fileName;
            this.content = content;
            this.type = type;
        }

        public String getFileName() {
            return fileName;
        }
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public byte[] getContent() {
            return content;
        }
        public void setContent(byte[] content) {
            this.content = content;
        }

        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }
    }
}
