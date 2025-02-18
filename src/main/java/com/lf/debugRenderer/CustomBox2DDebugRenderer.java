package com.lf.debugRenderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.JointDef.JointType;
import com.badlogic.gdx.physics.box2d.Shape.Type;
import com.badlogic.gdx.physics.box2d.joints.PulleyJoint;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.lf.entities.Arrow;
import com.lf.entities.Enemy;
import com.lf.entities.Tower;

import java.util.Iterator;

/**
 * TODO：重写此类只是为了箭矢、防御塔、敌人的刚体无需显示，后期优化项目
 */
public class CustomBox2DDebugRenderer implements Disposable {
    protected ShapeRenderer renderer;
    private static final Vector2[] vertices = new Vector2[1000];
    private static final Vector2 lower = new Vector2();
    private static final Vector2 upper = new Vector2();
    private static final Array<Body> bodies = new Array();
    private static final Array<Joint> joints = new Array();
    private boolean drawBodies;
    private boolean drawJoints;
    private boolean drawAABBs;
    private boolean drawInactiveBodies;
    private boolean drawVelocities;
    private boolean drawContacts;
    public final Color SHAPE_NOT_ACTIVE;
    public final Color SHAPE_STATIC;
    public final Color SHAPE_KINEMATIC;
    public final Color SHAPE_NOT_AWAKE;
    public final Color SHAPE_AWAKE;
    public final Color JOINT_COLOR;
    public final Color AABB_COLOR;
    public final Color VELOCITY_COLOR;
    private static Vector2 t = new Vector2();
    private static Vector2 axis = new Vector2();
    private final Vector2 f;
    private final Vector2 v;
    private final Vector2 lv;

    public CustomBox2DDebugRenderer() {
        this(true, true, false, true, false, true);
    }

    public CustomBox2DDebugRenderer(boolean drawBodies, boolean drawJoints, boolean drawAABBs, boolean drawInactiveBodies, boolean drawVelocities, boolean drawContacts) {
        this.SHAPE_NOT_ACTIVE = new Color(0.5F, 0.5F, 0.3F, 1.0F);
        this.SHAPE_STATIC = new Color(0.5F, 0.9F, 0.5F, 1.0F);
        this.SHAPE_KINEMATIC = new Color(0.5F, 0.5F, 0.9F, 1.0F);
        this.SHAPE_NOT_AWAKE = new Color(0.6F, 0.6F, 0.6F, 1.0F);
        this.SHAPE_AWAKE = new Color(0.9F, 0.7F, 0.7F, 1.0F);
        this.JOINT_COLOR = new Color(0.5F, 0.8F, 0.8F, 1.0F);
        this.AABB_COLOR = new Color(1.0F, 0.0F, 1.0F, 1.0F);
        this.VELOCITY_COLOR = new Color(1.0F, 0.0F, 0.0F, 1.0F);
        this.f = new Vector2();
        this.v = new Vector2();
        this.lv = new Vector2();
        this.renderer = new ShapeRenderer();

        for(int i = 0; i < vertices.length; ++i) {
            vertices[i] = new Vector2();
        }

        this.drawBodies = drawBodies;
        this.drawJoints = drawJoints;
        this.drawAABBs = drawAABBs;
        this.drawInactiveBodies = drawInactiveBodies;
        this.drawVelocities = drawVelocities;
        this.drawContacts = drawContacts;
    }

    public void render(World world, Matrix4 projMatrix) {
        this.renderer.setProjectionMatrix(projMatrix);
        this.renderBodies(world);
    }

    private void renderBodies(World world) {
        this.renderer.begin(ShapeType.Line);
        if (this.drawBodies || this.drawAABBs) {
            world.getBodies(bodies);

            for (Body body : bodies) {
                Object userData = body.getUserData();
                //箭矢、防御塔、敌人的刚体无需显示 20250207-lf
                if (userData instanceof Arrow || userData instanceof Tower || userData instanceof Enemy) {
                    continue;
                }
                if (body.isActive() || this.drawInactiveBodies) {
                    this.renderBody(body);
                }
            }
        }

        if (this.drawJoints) {
            world.getJoints(joints);

            for (Joint joint : joints) {
                this.drawJoint(joint);
            }
        }

        this.renderer.end();
        if (this.drawContacts) {
            this.renderer.begin(ShapeType.Point);

            for (Contact contact : world.getContactList()) {
                this.drawContact(contact);
            }

            this.renderer.end();
        }

    }

    protected void renderBody(Body body) {
        Transform transform = body.getTransform();
        Array.ArrayIterator var3 = body.getFixtureList().iterator();

        while(var3.hasNext()) {
            Fixture fixture = (Fixture)var3.next();
            if (this.drawBodies) {
                this.drawShape(fixture, transform, this.getColorByBody(body));
                if (this.drawVelocities) {
                    Vector2 position = body.getPosition();
                    this.drawSegment(position, body.getLinearVelocity().add(position), this.VELOCITY_COLOR);
                }
            }

            if (this.drawAABBs) {
                this.drawAABB(fixture, transform);
            }
        }

    }

    private Color getColorByBody(Body body) {
        if (!body.isActive()) {
            return this.SHAPE_NOT_ACTIVE;
        } else if (body.getType() == BodyType.StaticBody) {
            return this.SHAPE_STATIC;
        } else if (body.getType() == BodyType.KinematicBody) {
            return this.SHAPE_KINEMATIC;
        } else {
            return !body.isAwake() ? this.SHAPE_NOT_AWAKE : this.SHAPE_AWAKE;
        }
    }

    private void drawAABB(Fixture fixture, Transform transform) {
        if (fixture.getType() == Type.Circle) {
            CircleShape shape = (CircleShape)fixture.getShape();
            float radius = shape.getRadius();
            vertices[0].set(shape.getPosition());
            transform.mul(vertices[0]);
            lower.set(vertices[0].x - radius, vertices[0].y - radius);
            upper.set(vertices[0].x + radius, vertices[0].y + radius);
            vertices[0].set(lower.x, lower.y);
            vertices[1].set(upper.x, lower.y);
            vertices[2].set(upper.x, upper.y);
            vertices[3].set(lower.x, upper.y);
            this.drawSolidPolygon(vertices, 4, this.AABB_COLOR, true);
        } else if (fixture.getType() == Type.Polygon) {
            PolygonShape shape = (PolygonShape)fixture.getShape();
            int vertexCount = shape.getVertexCount();
            shape.getVertex(0, vertices[0]);
            lower.set(transform.mul(vertices[0]));
            upper.set(lower);

            for(int i = 1; i < vertexCount; ++i) {
                shape.getVertex(i, vertices[i]);
                transform.mul(vertices[i]);
                lower.x = Math.min(lower.x, vertices[i].x);
                lower.y = Math.min(lower.y, vertices[i].y);
                upper.x = Math.max(upper.x, vertices[i].x);
                upper.y = Math.max(upper.y, vertices[i].y);
            }

            vertices[0].set(lower.x, lower.y);
            vertices[1].set(upper.x, lower.y);
            vertices[2].set(upper.x, upper.y);
            vertices[3].set(lower.x, upper.y);
            this.drawSolidPolygon(vertices, 4, this.AABB_COLOR, true);
        }

    }

    private void drawShape(Fixture fixture, Transform transform, Color color) {
        if (fixture.getType() == Type.Circle) {
            CircleShape circle = (CircleShape)fixture.getShape();
            t.set(circle.getPosition());
            transform.mul(t);
            this.drawSolidCircle(t, circle.getRadius(), axis.set(transform.vals[2], transform.vals[3]), color);
        } else if (fixture.getType() == Type.Edge) {
            EdgeShape edge = (EdgeShape)fixture.getShape();
            edge.getVertex1(vertices[0]);
            edge.getVertex2(vertices[1]);
            transform.mul(vertices[0]);
            transform.mul(vertices[1]);
            this.drawSolidPolygon(vertices, 2, color, true);
        } else if (fixture.getType() == Type.Polygon) {
            PolygonShape chain = (PolygonShape)fixture.getShape();
            int vertexCount = chain.getVertexCount();

            for(int i = 0; i < vertexCount; ++i) {
                chain.getVertex(i, vertices[i]);
                transform.mul(vertices[i]);
            }

            this.drawSolidPolygon(vertices, vertexCount, color, true);
        } else {
            if (fixture.getType() == Type.Chain) {
                ChainShape chain = (ChainShape)fixture.getShape();
                int vertexCount = chain.getVertexCount();

                for(int i = 0; i < vertexCount; ++i) {
                    chain.getVertex(i, vertices[i]);
                    transform.mul(vertices[i]);
                }

                this.drawSolidPolygon(vertices, vertexCount, color, false);
            }

        }
    }

    private void drawSolidCircle(Vector2 center, float radius, Vector2 axis, Color color) {
        float angle = 0.0F;
        float angleInc = ((float)Math.PI / 10F);
        this.renderer.setColor(color.r, color.g, color.b, color.a);

        for(int i = 0; i < 20; angle += angleInc) {
            this.v.set((float)Math.cos((double)angle) * radius + center.x, (float)Math.sin((double)angle) * radius + center.y);
            if (i == 0) {
                this.lv.set(this.v);
                this.f.set(this.v);
            } else {
                this.renderer.line(this.lv.x, this.lv.y, this.v.x, this.v.y);
                this.lv.set(this.v);
            }

            ++i;
        }

        this.renderer.line(this.f.x, this.f.y, this.lv.x, this.lv.y);
        this.renderer.line(center.x, center.y, 0.0F, center.x + axis.x * radius, center.y + axis.y * radius, 0.0F);
    }

    private void drawSolidPolygon(Vector2[] vertices, int vertexCount, Color color, boolean closed) {
        this.renderer.setColor(color.r, color.g, color.b, color.a);
        this.lv.set(vertices[0]);
        this.f.set(vertices[0]);

        for(int i = 1; i < vertexCount; ++i) {
            Vector2 v = vertices[i];
            this.renderer.line(this.lv.x, this.lv.y, v.x, v.y);
            this.lv.set(v);
        }

        if (closed) {
            this.renderer.line(this.f.x, this.f.y, this.lv.x, this.lv.y);
        }

    }

    private void drawJoint(Joint joint) {
        Body bodyA = joint.getBodyA();
        Body bodyB = joint.getBodyB();
        Transform xf1 = bodyA.getTransform();
        Transform xf2 = bodyB.getTransform();
        Vector2 x1 = xf1.getPosition();
        Vector2 x2 = xf2.getPosition();
        Vector2 p1 = joint.getAnchorA();
        Vector2 p2 = joint.getAnchorB();
        if (joint.getType() == JointType.DistanceJoint) {
            this.drawSegment(p1, p2, this.JOINT_COLOR);
        } else if (joint.getType() == JointType.PulleyJoint) {
            PulleyJoint pulley = (PulleyJoint)joint;
            Vector2 s1 = pulley.getGroundAnchorA();
            Vector2 s2 = pulley.getGroundAnchorB();
            this.drawSegment(s1, p1, this.JOINT_COLOR);
            this.drawSegment(s2, p2, this.JOINT_COLOR);
            this.drawSegment(s1, s2, this.JOINT_COLOR);
        } else if (joint.getType() == JointType.MouseJoint) {
            this.drawSegment(joint.getAnchorA(), joint.getAnchorB(), this.JOINT_COLOR);
        } else {
            this.drawSegment(x1, p1, this.JOINT_COLOR);
            this.drawSegment(p1, p2, this.JOINT_COLOR);
            this.drawSegment(x2, p2, this.JOINT_COLOR);
        }

    }

    private void drawSegment(Vector2 x1, Vector2 x2, Color color) {
        this.renderer.setColor(color);
        this.renderer.line(x1.x, x1.y, x2.x, x2.y);
    }

    private void drawContact(Contact contact) {
        WorldManifold worldManifold = contact.getWorldManifold();
        if (worldManifold.getNumberOfContactPoints() != 0) {
            Vector2 point = worldManifold.getPoints()[0];
            this.renderer.setColor(this.getColorByBody(contact.getFixtureA().getBody()));
            this.renderer.point(point.x, point.y, 0.0F);
        }
    }

    public boolean isDrawBodies() {
        return this.drawBodies;
    }

    public void setDrawBodies(boolean drawBodies) {
        this.drawBodies = drawBodies;
    }

    public boolean isDrawJoints() {
        return this.drawJoints;
    }

    public void setDrawJoints(boolean drawJoints) {
        this.drawJoints = drawJoints;
    }

    public boolean isDrawAABBs() {
        return this.drawAABBs;
    }

    public void setDrawAABBs(boolean drawAABBs) {
        this.drawAABBs = drawAABBs;
    }

    public boolean isDrawInactiveBodies() {
        return this.drawInactiveBodies;
    }

    public void setDrawInactiveBodies(boolean drawInactiveBodies) {
        this.drawInactiveBodies = drawInactiveBodies;
    }

    public boolean isDrawVelocities() {
        return this.drawVelocities;
    }

    public void setDrawVelocities(boolean drawVelocities) {
        this.drawVelocities = drawVelocities;
    }

    public boolean isDrawContacts() {
        return this.drawContacts;
    }

    public void setDrawContacts(boolean drawContacts) {
        this.drawContacts = drawContacts;
    }

    public static Vector2 getAxis() {
        return axis;
    }

    public static void setAxis(Vector2 axis) {
        com.badlogic.gdx.physics.box2d.Box2DDebugRenderer.setAxis(axis);
    }

    public void dispose() {
        this.renderer.dispose();
    }
}
