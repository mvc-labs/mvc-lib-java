package com.mvcj.utils;

import android.util.Log;

import com.showpay.showmoneywalletsdk.model.Utxo;
import com.showpay.showmoneywalletsdk.org.mvcj.core.Coin;
import com.showpay.showmoneywalletsdk.org.mvcj.core.Transaction;
import com.showpay.showmoneywalletsdk.org.mvcj.core.TransactionInput;
import com.showpay.showmoneywalletsdk.org.mvcj.script.ScriptBuilder;
import com.showpay.showmoneywalletsdk.org.mvcj.script.ScriptOpCodes;
import com.showpay.showmoneywalletsdk.util.ByteUtil;
import com.showpay.showmoneywalletsdk.wallet.CalculationUtil;
import com.showpay.showmoneywalletsdk.wallet.LocalUtxoManager;
import com.showpay.showmoneywalletsdk.wallet.WalletManager;

import java.util.List;

/**
 * Author: ALuo
 * Date: 2022/5/20
 * Time: 10:12
 * Description:
 */
public class Utils {


    private static final String TAG = "Utils_s";

    public static Double feeb = 0.05;


    public static byte[] hexToByteArray(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            //奇数
            hexlen++;
            result = new byte[hexlen / 2];
            inHex = "0" + inHex;
        } else {
            //偶数
            result = new byte[hexlen / 2];
        }

        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }


    /**
     * Hex z字符串转为byte
     *
     * @param inHex
     * @return
     */
    public static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    public static boolean getFeeCanBroadCast(String rawTx, double feeb) {
//        double feeb = 0.5;
        boolean isPass = false;
        double fee = 0;
        long outAmount = 0;
        long inputAmount = 0;
        long txMessageSize = 0;
        byte[] bytes = hexToByteArray(rawTx);

        Transaction transaction = new Transaction(WalletManager.getParams(), bytes);
        txMessageSize = transaction.getMessageSize();
        if (transaction != null) {
            outAmount = transaction.getOutputSum().getValue();
            List<TransactionInput> transactionInputs = transaction.getInputs();
            if (transactionInputs != null) {
                for (int i = 0; i < transactionInputs.size(); i++) {
                    TransactionInput transactionInput = transactionInputs.get(i);
                    Log.i(TAG, "onBuild: index   ： " + transactionInput.getOutpoint().getIndex());
                    Log.i(TAG, "onBuild: getHash   ： " + transactionInput.getOutpoint().getHash().toString());

                    List<Utxo> loCalUtxoList = LocalUtxoManager.getInstance().getLocalUtxoList();
                    for (int j = 0; j < loCalUtxoList.size(); j++) {
                        Utxo utxo = loCalUtxoList.get(j);
                        if (utxo != null) {
                            Log.i(TAG, "utxo: index   ： " + utxo.getUtxo_index());
                            Log.i(TAG, "utxo: getHash   ： " + utxo.getTransactionId());
                            if (utxo.getUtxo_index() == transactionInput.getOutpoint().getIndex() && utxo.getTransactionId().equals(transactionInput.getOutpoint().getHash().toString())) {
                                inputAmount = inputAmount + Long.valueOf(utxo.getAmount());
                                Log.i(TAG, "getFeeCanBroadCast: " + utxo.getAmount());
                            }
                        }

                    }
                }
            }
            Log.i(TAG, "getFeeb: 输入金额： inputAmount: " + inputAmount);
            Log.i(TAG, "getFeeb: 输出金额： outAmount: " + outAmount);
            fee = inputAmount - outAmount;
            Log.i(TAG, "getFeeb: 手续费： fee: " + outAmount);
            double myFeeb = CalculationUtil.div(fee, txMessageSize);
            Log.i(TAG, "getFeeb: 计算实际费率： fee: " + myFeeb);
            return myFeeb < feeb ? false : true;
        }
        return isPass;
    }


    static boolean isCan = false;

    public static boolean getFeeCanPass(Transaction transaction, long inputAmount) {
        feeb = CalculationUtil.div(WalletManager.numerator_standard, WalletManager.denominator_standard);
        Log.i(TAG, "getFeeCanPass: 设定的费率是 ： " + feeb);
        long outAmount = transaction.getOutputSum().getValue();
        long txMessageSize = transaction.getMessageSize();
        Log.i(TAG, "getFeeCanPass: 计算的体积是  ： " + txMessageSize);
        double fee = inputAmount - outAmount;
        Double myFeeb = CalculationUtil.div(fee, txMessageSize);
        Log.i(TAG, "getFeeCanPass: 输入：" + inputAmount);
        Log.i(TAG, "getFeeCanPass: 输出：" + outAmount);
        Log.i(TAG, "getFeeCanPass:手续费：" + fee);
        Log.i(TAG, "getFeeCanPass: 计算的费率：" + myFeeb);
        return myFeeb < feeb ? false : true;

/*
        DecimalFormat df = new DecimalFormat("######0.00");
        String s = df.format(myFeeb);
        double myFeeb2 = Double.valueOf(s);

        if ( Double.doubleToLongBits(myFeeb2) > Double.doubleToLongBits(feeb)){
            isCan=true;
        }else {
            isCan=false;
        }

        if (!isCan){
            WalletManager.numerator= WalletManager.numerator+4;
        }else {
            WalletManager.setFeeComputeRate(6);
        }
        Log.i(TAG, "getFeeCanPass: 是否通过："+isCan);
        return isCan;*/
    }


        public static void addOpReturn (Transaction transaction){
            String[] opDatas = {"androidShow"};
            if (opDatas.length != 0) {
                byte[] op0 = {};
                ScriptBuilder script = new ScriptBuilder().data(op0).op(ScriptOpCodes.OP_RETURN);
                for (String data : opDatas) {
                    byte[] scriptByte = {};
                    scriptByte = ByteUtil.concat(scriptByte, data.getBytes());
                    script.data(scriptByte);
                }
                transaction.addOutput(Coin.ZERO, script.build());
            }
        }




    }
