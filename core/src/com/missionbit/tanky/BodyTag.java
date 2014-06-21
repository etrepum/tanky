package com.missionbit.tanky;

/**
 * Created by bob on 6/20/14.
 */
public class BodyTag {
    public enum BodyType {
        TANK, BULLET, BodyType, TERRAIN
    }

    public final BodyType type;
    public final String name;

    BodyTag(BodyType type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public String toString() {
        String tagName;
        switch (type) {
            case TANK: tagName = "TANK"; break;
            case BULLET: tagName = "BULLET"; break;
            case TERRAIN: tagName = "TERRAIN"; break;
            default: throw new Error("Invalid BodyType");
        }
        return "BodyType(" + tagName + ", \"" + name + "\")";
    }

}
