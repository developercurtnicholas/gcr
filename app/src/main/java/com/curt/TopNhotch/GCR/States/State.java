package com.curt.TopNhotch.GCR.States;
import android.util.Log;
import java.util.ArrayList;
import java.util.LinkedHashMap;


public abstract class State{

    private ArrayList<State> preRequisites = new ArrayList<State>();
    protected Context CONTEXT;
    protected String KEY = this.getClass().getName();
    private boolean noPreReqs = false;


    public State(Context context){
        if(contextNull(context))
            return;
        this.CONTEXT = context;
        this.setPreReqs(this.CONTEXT);
        this.setKeyName();
    }
    private boolean contextNull(Context context){
        if(context == null){
            try {
                throw new Exception("The context can't be null");
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
        }else{
            return false;
        }
    }
    public void execute(){

        if(this.hasAllPreReq()){
            doAction();
        }else{
            String reqs = printPreReqs();
            throw new IllegalStateException("To execute the '"+this.getClass().getName()
                    +"' we need to be in the"
                    + " following states: " + reqs);
        }
        this.CONTEXT.states.put(this.KEY, this);
        //TODO: remove because the state class won't be reusable
        Log.i("STATES: ",this.CONTEXT.toString());
    }

    public String getKEY(){

        return this.KEY;
    }

    public String printPreReqs(){
        String msg = "";
        for(State s : this.preRequisites){
            msg += s.KEY + ", ";
        }
        return msg;
    }

    protected void setNoPreReqs(){
        this.noPreReqs = true;
    }

    public boolean hasAllPreReq(){
        if(this.preRequisites.size() <= 0 && !this.noPreReqs){
            throw new UnsupportedOperationException(
                    "No Prequisited defined for "+this.getClass().getName()
                         + " if none are needed call 'setNoPreReqs' in the 'setPreReqs'"
                            +" method"
            );
        }
        for(State s : this.preRequisites){
            if(!this.CONTEXT.states.containsKey(s.KEY))
                return false;
        }
        return true;
    }



    public void addPreRequisite(State state){
        this.preRequisites.add(state);
    }

    protected abstract void doAction();
    protected abstract void setPreReqs(Context context);
    protected abstract void setKeyName();

    public static class Context {
        public LinkedHashMap<String,State> states = new LinkedHashMap<String,State>();
        public String toString(){
            String stateTransitions = "";

            String[] array = new String[states.keySet().size()];
            states.keySet().toArray(array);
            for (String key : array) {
                stateTransitions += key + " => ";
            }
            return stateTransitions;
        }
    }
}
