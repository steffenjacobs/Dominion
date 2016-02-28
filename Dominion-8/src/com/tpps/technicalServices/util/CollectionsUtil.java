package com.tpps.technicalServices.util;

import java.util.ArrayList;

import com.tpps.application.game.logic.Action;
import com.tpps.application.game.logic.Type;

public final class CollectionsUtil {

	public static ArrayList<Action> arrayListAction(Action... actions) {
		ArrayList<Action> resultList = new ArrayList<Action>();
		for (Action action : actions) {
			resultList.add(action);
		}
		return resultList;
	}

	public static ArrayList<Type> arrayListType(Type... types) {
		ArrayList<Type> resultList = new ArrayList<Type>();
		for (Type type : types) {
			resultList.add(type);
		}
		return resultList;
	}
}
