package model;

import java.util.HashMap;

public class ModelAndView {

    private String view;
    private HashMap<String,Object> attributes;


    public ModelAndView(String view) {
        this.view = view;
        this.attributes = new HashMap<>();
    }

    public ModelAndView(String view,HashMap<String , Object > attributes) {
        this.view = view;
        this.attributes = attributes;
    }

    public void addAttributes(String name,Object value) {
        this.attributes.put(name, value);
    }

    public String getView() {
        return view;
    }
    public HashMap<String, Object> getAttributes() {
        return attributes;
    }
}
