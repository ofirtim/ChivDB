package dev.millenialsoftwares.utils.caller;

import java.util.Map;

public interface HashedData {

    public Map<String, Object> getChildren();

    public Map<String, Object> removeChild(String id);

    public Map<String, Object> getParentsSorted();


}
