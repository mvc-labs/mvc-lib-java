package com.mvcj.core.listeners;

import com.showpay.showmoneywalletsdk.org.mvcj.core.EmptyMessage;
import com.showpay.showmoneywalletsdk.org.mvcj.core.NetworkParameters;

/**
 * Created by HashEngineering on 8/11/2017.
 */
public class FeeFilterMessage extends EmptyMessage{
    public FeeFilterMessage(NetworkParameters params){
        super(params);
    }
}
