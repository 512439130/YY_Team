package com.yy.http.okhttp.builder;

import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.request.OtherRequest;
import com.yy.http.okhttp.request.RequestCall;

/**
 * Created by yy on 16/3/2.
 */
public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers,id).build();
    }
}
