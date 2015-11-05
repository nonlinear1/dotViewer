package com.example.jakob.PointCloudVisualizer.GlObjects;

import android.opengl.Matrix;

import com.example.jakob.PointCloudVisualizer.util.MatrixHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.example.jakob.PointCloudVisualizer.BasicActivityRender.COLOR_COMPONENT_COUNT;
import static com.example.jakob.PointCloudVisualizer.BasicActivityRender.POSITION_COMPONENT_COUNT;
import static android.opengl.GLES20.GL_FLOAT;
import static com.example.jakob.PointCloudVisualizer.util.BufferHelper.buildFloatBuffer;

public abstract class ModelGL {
    protected FloatBuffer mVertexBuffer;
    private FloatBuffer mColorBuffer;
    private float scale;
    public int vertexCount;

    float[] modelMatrix;
    float[] transMatrix;
    float[] rotationMatrix;
    float[] scaleMatrix;
    float[] centerMatrix;

    ModelGL(FloatBuffer vertices){
        scale = 1;
        mVertexBuffer = vertices;
        mVertexBuffer.position(0);
        initMatrices();
        vertexCount = mVertexBuffer.capacity() / 4;
        float[] centroid = getCentroid();
        Matrix.translateM(centerMatrix, 0, -centroid[0], -centroid[1], -centroid[2]);
    }

    ModelGL(FloatBuffer vertices, FloatBuffer colors) {
        this(vertices);
        mColorBuffer = colors;
        mColorBuffer.position(0);
    }

    ModelGL(float[] vertices) {
        this(buildFloatBuffer(vertices, 3));
    }

    ModelGL(float[] vertices, float[] colors) {
        this(buildFloatBuffer(vertices, 4), buildFloatBuffer(colors, 4));
    }

    private void initMatrices(){
        scaleMatrix = new float[16];
        modelMatrix = new float[16];
        transMatrix = new float[16];
        centerMatrix = new float[16];
        rotationMatrix = new float[16];
        Matrix.setIdentityM(transMatrix, 0);
        Matrix.setIdentityM(rotationMatrix, 0);
        Matrix.setIdentityM(centerMatrix, 0);
        Matrix.setIdentityM(scaleMatrix, 0);
        Matrix.scaleM(scaleMatrix, 0, scale, scale, scale);
    }


    public void bindVertex(int aPositionLocation){
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, 0, this.mVertexBuffer);
        glEnableVertexAttribArray(aPositionLocation);
    }

    public void bindColor(int aColorLocation){
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT,
                false, 0, this.mColorBuffer);
        glEnableVertexAttribArray(aColorLocation);
    }

    public float[] getCentroid() {
        float[] centroid = {0, 0, 0};
        mVertexBuffer.rewind();
        while (mVertexBuffer.hasRemaining()) {
            centroid[0] += mVertexBuffer.get();
            centroid[1] += mVertexBuffer.get();
            centroid[2] += mVertexBuffer.get();
        }
        centroid[0] /= vertexCount;
        centroid[1] /= vertexCount;
        centroid[2] /= vertexCount;
        mVertexBuffer.rewind();
        return centroid;
    }

    public float[] getModelMatrix() {
        MatrixHelper.multMatrices(modelMatrix,
                transMatrix,
                rotationMatrix,
                scaleMatrix,
                centerMatrix
        );
        return modelMatrix;
    }

    public void rotate(float[] angles){
        MatrixHelper.rotationMatrix(rotationMatrix, angles);
    }

    public void scale(float scale){
        scaleMatrix[0]= scale;
        scaleMatrix[5]= scale;
        scaleMatrix[10]= scale;
    }

    public abstract void draw();
}