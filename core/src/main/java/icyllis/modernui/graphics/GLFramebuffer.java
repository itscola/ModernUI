/*
 * Modern UI.
 * Copyright (C) 2019-2021 BloCamLimb. All rights reserved.
 *
 * Modern UI is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Modern UI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Modern UI. If not, see <https://www.gnu.org/licenses/>.
 */

package icyllis.modernui.graphics;

import icyllis.modernui.graphics.texture.GLRenderbuffer;
import icyllis.modernui.graphics.texture.GLTexture;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.lwjgl.BufferUtils;

import javax.annotation.Nonnull;
import java.nio.FloatBuffer;

import static icyllis.modernui.graphics.GLWrapper.*;

/**
 * This class represents a framebuffer object. It is used for creation of
 * user-defined framebuffers compared to the default framebuffer, whose object
 * is a collection of attachments, for off-screen rendering or post-processing.
 * <p>
 * For post-processing, attach this to a set of textures otherwise to render
 * buffers. To output this framebuffer to screen, draw the attached textures
 * or copy the renderbuffer pixels to the default framebuffer that preserved
 * by window graphics context.
 * <p>
 * Losing the reference of the object will delete the framebuffer and all
 * attachments automatically.
 *
 * @see <a href="https://www.khronos.org/opengl/wiki/Framebuffer_Object">Framebuffer Object</a>
 */
//TODO WIP
public final class GLFramebuffer extends GLObject {

    private int mWidth;
    private int mHeight;

    private Int2ObjectMap<GLObject> mAttachments;

    //private int mMsaaLevel = 0;

    private final FloatBuffer mClearColor = BufferUtils.createFloatBuffer(4);

    public GLFramebuffer(int width, int height) {
        mWidth = Math.max(1, width);
        mHeight = Math.max(1, height);
    }

    @Override
    public int get() {
        if (ref == null) {
            ref = new FramebufferRef(this);
            mAttachments = new Int2ObjectArrayMap<>();
        }
        return ref.object;
    }

    /**
     * Binds this framebuffer object to both draw and read target.
     */
    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, get());
    }

    public void bindDraw() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, get());
    }

    public void bindRead() {
        glBindFramebuffer(GL_READ_FRAMEBUFFER, get());
    }

    public void attachTexture(int attachmentPoint, int internalFormat) {
        GLTexture texture = new GLTexture(GL_TEXTURE_2D_MULTISAMPLE);
        texture.allocate2DMS(internalFormat, mWidth, mHeight, 4);
        glNamedFramebufferTexture(get(), attachmentPoint, texture.get(), 0);
        mAttachments.put(attachmentPoint, texture);
    }

    public void attachRenderbuffer(int attachmentPoint, int internalFormat) {
        GLRenderbuffer renderbuffer = new GLRenderbuffer();
        renderbuffer.allocate(internalFormat, mWidth, mHeight, 4);
        glNamedFramebufferRenderbuffer(get(), attachmentPoint, GL_RENDERBUFFER, renderbuffer.get());
        mAttachments.put(attachmentPoint, renderbuffer);
    }

    public void removeAttachment(int attachmentPoint) {
        if (mAttachments.remove(attachmentPoint) != null) {
            glNamedFramebufferTexture(get(), attachmentPoint, DEFAULT_TEXTURE, 0);
        }
    }

    public void clearAttachments() {
        int framebuffer = get();
        for (int point : mAttachments.keySet()) {
            glNamedFramebufferTexture(framebuffer, point, DEFAULT_TEXTURE, 0);
        }
        mAttachments.clear();
    }

    public void reset(int width, int height) {
        resize(width, height);
        clearColorBuffer();
        clearDepthStencilBuffer();
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    /**
     * Reallocate all attachments to the new size, if changed.
     */
    public void resize(int width, int height) {
        if (mWidth == width && mHeight == height) {
            return;
        }
        mWidth = width;
        mHeight = height;
        for (var entry : mAttachments.int2ObjectEntrySet()) {
            GLObject obj = entry.getValue();
            if (obj instanceof GLTexture) {
                GLTexture texture = (GLTexture) obj;
                int internalFormat = glGetTextureLevelParameteri(texture.get(), 0, GL_TEXTURE_INTERNAL_FORMAT);
                texture.close();
                texture.allocate2DMS(internalFormat, width, height, 4);
                glNamedFramebufferTexture(get(), entry.getIntKey(), texture.get(), 0);
            } else if (obj instanceof GLRenderbuffer) {
                GLRenderbuffer renderbuffer = (GLRenderbuffer) obj;
                int internalFormat = glGetNamedRenderbufferParameteri(renderbuffer.get(),
                        GL_RENDERBUFFER_INTERNAL_FORMAT);
                renderbuffer.close();
                renderbuffer.allocate(internalFormat, width, height, 4);
                glNamedFramebufferRenderbuffer(get(), entry.getIntKey(), GL_RENDERBUFFER, renderbuffer.get());
            }
        }
    }

    /**
     * Set the color used for {@link #clearColorBuffer()}, default clear color is (0,0,0,0).
     */
    public void setClearColor(float r, float g, float b, float a) {
        mClearColor.put(r).put(g).put(b).put(a).flip();
    }

    /**
     * Clear the current color buffer set by {@link #setDrawBuffer(int)} to the color
     * set by {@link #setClearColor(float, float, float, float)}, default clear color is (0,0,0,0).
     */
    public void clearColorBuffer() {
        // here drawbuffer is zero, because setDrawBuffer only set the buffer with index 0
        glClearNamedFramebufferfv(get(), GL_COLOR, 0, mClearColor);
    }

    /**
     * Clear the current depth buffer set by {@link #setDrawBuffer(int)} to
     * 1.0f, and stencil buffer to 0.
     */
    public void clearDepthStencilBuffer() {
        // for depth or stencil, the drawbuffer must be 0
        glClearNamedFramebufferfi(get(), GL_DEPTH_STENCIL, 0, 1.0f, 0);
    }

    /**
     * Set the color buffer for <code>layout(location = 0) out vec4 fragColor</code>.
     * That means the color buffer index is 0.
     * <p>
     * Note that only GL_COLOR_ATTACHMENT[x] or GL_NONE is accepted by a framebuffer
     * object. Values such as GL_FRONT_LEFT, GL_BACK are only accepted by the default
     * framebuffer (reserved by the window).
     *
     * @param buffer enum buffer
     */
    public void setDrawBuffer(int buffer) {
        glNamedFramebufferDrawBuffer(get(), buffer);
    }

    /**
     * Returns the attached texture with the given attachment point.
     *
     * @param attachmentPoint specify an attachment point
     * @return the texture name
     * @throws IllegalArgumentException attachment is not a texture or detached
     */
    @Nonnull
    public GLTexture getAttachedTexture(int attachmentPoint) {
        AutoCloseable a = mAttachments.get(attachmentPoint);
        if (a instanceof GLTexture) {
            return ((GLTexture) a);
        }
        throw new IllegalArgumentException();
    }

    @Nonnull
    public GLRenderbuffer getAttachedRenderbuffer(int attachmentPoint) {
        AutoCloseable a = mAttachments.get(attachmentPoint);
        if (a instanceof GLRenderbuffer) {
            return (GLRenderbuffer) a;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public void close() {
        super.close();
        if (mAttachments != null) {
            mAttachments.values().forEach(GLObject::close);
            mAttachments = null;
        }
    }

    private static final class FramebufferRef extends Ref {

        private FramebufferRef(@Nonnull GLFramebuffer owner) {
            super(owner, glCreateFramebuffers());
        }

        @Override
        public void run() {
            deleteFramebufferAsync(object, this);
        }
    }
}