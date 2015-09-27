package org.tastefuljava.gianadda.ui.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListenerList {
    private static final Logger LOG
            = Logger.getLogger(ListenerList.class.getName());

    private final List<Object> list = new ArrayList<>();
    private boolean disabled;

    public ListenerList() {
    }

    public void addListener(Object listener) {
        list.add(listener);
    }

    public void removeListener(Object listener) {
        list.remove(listener);
    }

    public boolean isNotificationRequired() {
        return !disabled && !list.isEmpty();
    }

    public void disable() {
        if (disabled) {
            throw new IllegalStateException("Already disabled");
        }
        disabled = true;
    }

    public void enable() {
        if (!disabled) {
            throw new IllegalStateException("Already enabled");
        }
        disabled = false;
    }

    public <T> T getNotifier(Class<T> intf) {
        ClassLoader cl = intf.getClassLoader();
        return intf.cast(Proxy.newProxyInstance(cl, new Class[] {intf},
                new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args)
                    throws Throwable {
                if (disabled) {
                    return null;
                }
                return actualInvoke(method, args);
            }
        }));
    }

    public Object notify(String name, Object... args) {
        Object result = null;
        for (Object listener: list) {
            Method method = findMethod(listener, name, args);
            if (method != null) {
                try {
                    result = method.invoke(listener, args);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    LOG.log(Level.SEVERE, "Error invoking listener method", e);
                }
            }
        }
        return result;
    }

    private Method findMethod(Object instance, String name, Object args[]) {
        Method methods[] = instance.getClass().getMethods();
        for (Method method: methods) {
            if (name.equals(method.getName())) {
                Class formalTypes[] = method.getParameterTypes();
                if (args == null) {
                    if (formalTypes.length == 0) {
                        return method;
                    }
                } else if (args.length == formalTypes.length) {
                    return method;
                }
            }
        }
        return null;
    }

    private Object actualInvoke(Method method, Object args[]) {
        Object result = null;
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Object listener = it.next();
            try {
                result = method.invoke(listener, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                LOG.log(Level.SEVERE, "Error invoking listener method", e);
            }
        }
        return result;
    }
}
