package io.github.sunnybird.androlua;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.keplerproject.luajava.JavaFunction;
import org.keplerproject.luajava.LuaException;
import org.keplerproject.luajava.LuaState;
import org.keplerproject.luajava.LuaStateFactory;

public class MainActivity extends AppCompatActivity {


    private LuaState mLuaState;
    private StringBuilder  output =   new StringBuilder();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLua();
        regJava2lua();


        String luaScript =
                "javaExecute('string',1) \n"+
                 "javaCall()";

        try {
            evalLua(luaScript);
        } catch (LuaException e) {
            e.printStackTrace();
        }


    }


    private void initLua() {

        mLuaState = LuaStateFactory.newLuaState();
        if (mLuaState == null) {
            Toast.makeText(this, "init lua fail", Toast.LENGTH_LONG).show();
            return;
        }
        mLuaState.openLibs();

    }


    // 注册 Java 方法到 Lua
    private void regJava2lua() {

        try {
            JavaFunction javaExecute = new JavaFunction(mLuaState) {
                @Override
                public int execute() throws LuaException {
                    Log.d("javaExecute","execute");
                    for (int i = 2; i <= mLuaState.getTop(); i++) {
                        int type = mLuaState.type(i);
                        // 解析参数类型
                        String stype = mLuaState.typeName(type);
                        String val = "";
                        if (stype.equals("userdata")) {
                            Object obj = mLuaState.toJavaObject(i);
                            if (obj != null)
                                val = obj.toString();
                        } else if (stype.equals("boolean")) {
                            val = mLuaState.toBoolean(i) ? "true" : "false";
                        } else {
                            val = mLuaState.toString(i);
                        }

                        Log.d("javaExecute ", "val == " + val);
                    }
                    return 0;
                }
            };

            javaExecute.register("javaExecute");

            JavaFunction javaCall = new JavaFunction(mLuaState) {
                @Override
                public int execute() throws LuaException {

                    Log.d("javaCall","execute");
                    return 0;
                }
            };

            javaCall .register("javaCall");

        }catch (Exception e){



        }
    }


    // 执行 lua 脚本
    private String evalLua(String src) throws LuaException {
        mLuaState.setTop(0);
        int ok = mLuaState.LloadString(src);
        if (ok == 0) {
            mLuaState.getGlobal("debug");
            mLuaState.getField(-1, "traceback");
            mLuaState.remove(-2);
            mLuaState.insert(-2);
            ok = mLuaState.pcall(0, 0, -2);
            if (ok == 0) {
                String res = output.toString();
                output.setLength(0);
                return res;
            }
        }
        throw new LuaException(errorReason(ok) + ": " + mLuaState.toString(-1));
        //return null;

    }


    private String errorReason(int error) {
        switch (error) {
            case 4:
                return "Out of memory";
            case 3:
                return "Syntax error";
            case 2:
                return "Runtime error";
            case 1:
                return "Yield error";
        }
        return "Unknown error " + error;
    }
}
