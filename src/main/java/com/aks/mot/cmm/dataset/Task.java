package com.aks.mot.cmm.dataset;

import java.util.HashMap;
import java.util.Map;

public interface Task<T extends Map> {

	public T run(HashMap<String, Object> returnMap);
}
