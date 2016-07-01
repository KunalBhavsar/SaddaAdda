package com.emiadda.core;

import java.io.Serializable;

/**
 * Created by Kunal Bhavsar on 16/3/16.
 */
public class EABase implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private long version;
    private long active;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public long getActive() {
        return active;
    }

    public void setActive(long active) {
        this.active = active;
    }
}
