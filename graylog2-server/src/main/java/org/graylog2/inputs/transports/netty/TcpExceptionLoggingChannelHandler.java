/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.inputs.transports.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.ReadTimeoutException;
import org.graylog2.plugin.inputs.MessageInput;
import org.slf4j.Logger;

public class TcpExceptionLoggingChannelHandler extends ExceptionLoggingChannelHandler {
    private final boolean isKeepAliveEnabled;

    public TcpExceptionLoggingChannelHandler(MessageInput input, boolean isKeepAliveEnabled, Logger logger) {
        super(input, logger);
        this.isKeepAliveEnabled = isKeepAliveEnabled;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        boolean isAKeepAliveTimeout =
                this.isKeepAliveEnabled && cause instanceof ReadTimeoutException;

        if(isAKeepAliveTimeout){

            if(logger.isTraceEnabled()){
                logger.trace("KeepAlive timed out in Input [{}/{}] (channel {})",
                        input.getName(),
                        input.getId(),
                        ctx.channel());
            }

            ctx.close();
        } else {
            super.exceptionCaught(ctx, cause);
        }
    }
}
