package dev.millenialsoftwares.utils.caller;

import java.util.Map;

public interface SimpleData extends Map<String, Object> {

    public Object get(String id);

    public Boolean getAsBoolean(String id);

    public String getAsString(String id);

    public Integer getAsInteger(String id);

    public Short getAsShort(String id);

    public Long getAsLong(String id);

    public Number geAsNumber(String id);

    public Float getAsFloat(String id);

    public Double getAsDouble(String id);

    public <T> T getAsType(Class<T> type, String id);

}
