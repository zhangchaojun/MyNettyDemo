package com.example.mynettydemo.ssalinstance.handler;

import android.util.Log;

import com.example.mynettydemo.ssalinstance.entity.bo.lud.ApplicationData;
import com.example.mynettydemo.ssalinstance.entity.impl.DefaultSSALParser;
import com.example.mynettydemo.ssalinstance.entity.impl.SSALMessage;
import com.example.mynettydemo.ssalinstance.enums.FCCodeBinEnum;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SSALContentBizAdapterHandler extends SimpleChannelInboundHandler<SSALMessage> {
    private static final Logger logger = LoggerFactory.getLogger(SSALContentBizAdapterHandler.class);
    private static Map<String, List<SSALMessage>> unCompleteBizMsgStore = new HashMap<>();
    private static final String TAG = "cj4";
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SSALMessage msg) throws Exception {
        String bizId = msg.getFrameSequenceHex();
        ApplicationData applicationData = (ApplicationData) msg.getLinkUserData();
        int currentPkgNum = applicationData.getCurrentPkgNum();
        int pkgNum = applicationData.getPkgNum();
        Log.e(TAG, "channelRead0:received msg: bizId = {"+bizId+"}, currentPkgNum = {"+currentPkgNum+"}, pkgNum = {"+pkgNum+"}");
        if(currentPkgNum + 1 == pkgNum && pkgNum == 1) {
            logger.trace("single pkg content");
            if (FCCodeBinEnum.ZERO.equals(msg.getControlData().getFunctionCode().getStartUpSymbol())) {
                // 下行的响应业务报文需要转换回原始响应报文
                ByteBuf byteBuf = DefaultSSALParser.instance().removeSSAL(msg);
                ReferenceCountUtil.retain(byteBuf);
                ctx.fireChannelRead(byteBuf);
            }else {
                // 上行数据按照顺序向后写
                unCompleteBizMsgStore.remove(bizId);
                ctx.fireChannelRead(msg);
            }

        }else {
//            List<SSALMessage> ssalMessageList = unCompleteBizMsgStore.computeIfAbsent(bizId, new ArrayList<>());
            List<SSALMessage> ssalMessageList = unCompleteBizMsgStore.get(bizId);
            if (ssalMessageList == null) {
                ssalMessageList = new  ArrayList<>();
                unCompleteBizMsgStore.put(bizId, ssalMessageList);
            }
//            List<SSALMessage> ssalMessageList = new ArrayList<>();

            ssalMessageList.add(msg);
            if (ssalMessageList.size() == applicationData.getPkgNum()) {
                logger.info("multi pkg content pkg completed");
                Collections.sort(ssalMessageList,new Comparator<SSALMessage>() {
                    @Override
                    public int compare(SSALMessage o1, SSALMessage o2) {
                        ApplicationData applicationData1 = (ApplicationData) o1.getLinkUserData();
                        ApplicationData applicationData2 = (ApplicationData) o2.getLinkUserData();
                        return applicationData1.getCurrentPkgNum() - applicationData2.getCurrentPkgNum();
                    }
                });
//                ssalMessageList.sort(new Comparator<SSALMessage>() {
//                    @Override
//                    public int compare(SSALMessage o1, SSALMessage o2) {
//                        ApplicationData applicationData1 = (ApplicationData) o1.getLinkUserData();
//                        ApplicationData applicationData2 = (ApplicationData) o2.getLinkUserData();
//                        return applicationData1.getCurrentPkgNum() - applicationData2.getCurrentPkgNum();
//                    }
//                });

                if (FCCodeBinEnum.ZERO.equals(msg.getControlData().getFunctionCode().getStartUpSymbol())) {
                    // 下行的响应业务报文需要转换回原始响应报文

                    ByteBuf byteBuf = DefaultSSALParser.instance().removeSSAL(ssalMessageList);
                    ReferenceCountUtil.retain(byteBuf);
                    ctx.fireChannelRead(byteBuf);
                }else {
                    // 上行数据按照顺序向后写
                    unCompleteBizMsgStore.remove(bizId);
                    for (SSALMessage msg0 : ssalMessageList) {
                        ctx.fireChannelRead(msg0);
                    }
                }
            }
        }
    }
}
