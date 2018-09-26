package model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

class KeyPath {
    final String[] fullKeyPath;
    final Map data;

    public KeyPath(String fullKeyPath, Map data) {
        this.fullKeyPath = fullKeyPath.toString().split("\\.");
        this.data = data;
    }

    public String deepestKey() {

        return fullKeyPath[fullKeyPath.length-1];
    }

    public void removeKeyFrom() {

        this.secureMap(this.fullKeyPath())
            .remove(this.fullKeyPath[this.fullKeyPath.length - 1]);
    }

    public Map asurePathFrom() {
        // Ensures path existence
        Map current = data;

        for(int i = 0; i< this.fullKeyPath.length - 1; ++i) {
            if ( current.get(this.fullKeyPath[i]) == null) {
                current.put(this.fullKeyPath[i], new HashMap<String,Object>());
            }

            try {
                current = (Map) current.get(this.fullKeyPath[i]);
            } catch (ClassCastException e) {
                // If cast faild => this.fullKeyPath[i] refers not map then
                // we return dummy map instance
                current = new HashMap();
            }
        }

        return current;

    }

    private Map secureMap(String keyName) {
        // Ensures path existence
        Map current = this.data;

        for(int i = 0; i< this.fullKeyPath.length - 1; ++i) {
            if ( current.get(this.fullKeyPath[i]) == null) {
                current = new HashMap(); // Dummy map
                break;
            }
            try {
                current = (Map) current.get(this.fullKeyPath[i]);
            } catch (ClassCastException e) {
                // If cast faild => this.fullKeyPath[i] refers not map then
                // we return dummy map instance
                current = new HashMap();
            }
        }

        return current;
    }

    public void alterWith(Object value) {

        // Ensures path existence
        Map current = this.secureMap( this.fullKeyPath());

        current.put(this.fullKeyPath[this.fullKeyPath.length - 1 ], value);

    }

    public Object deepestValue() {

        Map parent = this.secureMap( this.fullKeyPath() );

        return parent.get( this.fullKeyPath[this.fullKeyPath.length -1]);
    }

    private String fullKeyPath() {

        return Arrays.stream(this.fullKeyPath).collect(Collectors.joining("."));
    }
}
