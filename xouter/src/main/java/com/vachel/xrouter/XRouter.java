package com.vachel.xrouter;

import androidx.annotation.NonNull;

/**
 * Created by jianglixuan on 2020/7/10.
 * Describe:
 * 用法： 在Application中注册
 *          XRouter.getInstance().build(classInterface， classInterface1， classInterface2……)
 *                                .map(classImpl, classImpl1, classImpl2……);
 * 注意各模块对外暴露接口和实现该接口的实体对象分别一一对应
 *
 * 调用时直接 XRouter.getInstance().getService(InterfaceA.class)可以拿到注册时该接口对应的实体对象
 *
 */
public class XRouter {
    private static XRouter sInstance = null;
    private IServiceProvider mServiceProvider = null;

    public static XRouter getInstance() {
        if (sInstance == null) {
            synchronized (XRouter.class) {
                if (sInstance == null) {
                    sInstance = new XRouter();
                }
            }
        }
        return sInstance;
    }

    private XRouter() {
    }

    public Builder build(@NonNull Class... registeredClazz) {
        return new Builder(registeredClazz);
    }

    public class Builder {
        Class[] registeredClass;

        private Builder() {

        }

        private Builder(@NonNull Class... registeredClazz) {
            registeredClass = registeredClazz;
        }

        public Builder map(final Object... registeredClazzImpl) {
            if (registeredClass.length != registeredClazzImpl.length) {
                throw new RuntimeException("xRouter init error: registeredClazz length must equals registeredClazzImpl length");
            }
            mServiceProvider = new IServiceProvider() {
                @Override
                public <T> T getService(Class<T> clazz) {
                    for (int i = 0; i < registeredClass.length; i++) {
                        Class cellClazz = registeredClass[i];
                        if (clazz == cellClazz) {
                            return (T) registeredClazzImpl[i];
                        }
                    }
                    throw new RuntimeException("xRouter error: maybe you has't registered the service");
                }
            };
            return this;
        }
    }

    @NonNull
    public <T> T getService(Class<T> clazz) {
        if (mServiceProvider == null) {
            throw new RuntimeException("xRouter init error: has't init");
        }
        return mServiceProvider.getService(clazz);
    }

    public interface IServiceProvider {
        <T> T getService(Class<T> clazz);
    }

}
