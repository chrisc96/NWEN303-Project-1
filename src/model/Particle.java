package model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static model.Model.*;

public class Particle {
    public Particle(double mass, double speedX, double speedY, double x, double y) {
        this.mass = mass;
        this.speedX = speedX;
        this.speedY = speedY;
        this.x = x;
        this.y = y;
    }

    public Set<Particle> impacting = new HashSet<>();
    public double mass;
    public double speedX;
    public double speedY;
    public double x;
    public double y;

    public void move(Model m) {
        x += speedX / (timeFrame);
        y += speedY / (timeFrame);
        //uncomment the following to have particle bouncing on the boundary
        if(this.x<0){this.speedX*=-1;}
        if(this.y<0){this.speedY*=-1;}
        if(this.x> size){this.speedX*=-1;}
        if(this.y> size){this.speedY*=-1;}
    }

    public boolean isImpact(double dist, double otherMass) {
        if (Double.isNaN(dist)) {
            return true;
        }
        double distMass = Math.sqrt(mass) + Math.sqrt(otherMass);
        if (dist < distMass * distMass) {
            return true;
        }
        return false;
    }

    public boolean isImpact(Iterable<Particle> ps) {
        for (Particle p : ps) {
            if (this == p) {
                continue;
            }
            double dist = distance2(p);
            if (isImpact(dist, p.mass)) {
                return true;
            }
        }
        return false;
    }

    public double distance2(Particle p) {
        double distX = this.x - p.x;
        double distY = this.y - p.y;
        return distX * distX + distY * distY;
    }

    public void interact(Model m) {
        for (Particle p : m.p) {
            if (p == this) continue;
            double dirX = -Math.signum(this.x - p.x);
            double dirY = -Math.signum(this.y - p.y);
            double dist = distance2(p);
            if (isImpact(dist, p.mass)) {
                this.impacting.add(p);
                continue;
            }
            dirX = p.mass * gravitationalConstant * dirX / dist;
            dirY = p.mass * gravitationalConstant * dirY / dist;
            assert this.speedX <= lightSpeed : this.speedX;
            assert this.speedY <= lightSpeed : this.speedY;
            double newSpeedX = this.speedX + dirX;
            newSpeedX /= (1 + (this.speedX * dirX) / lightSpeed);
            double newSpeedY = this.speedY + dirY;
            newSpeedY /= (1 + (this.speedY * dirY) / lightSpeed);
            if (!Double.isNaN(dirX)) {
                this.speedX = newSpeedX;
            }
            if (!Double.isNaN(dirY)) {
                this.speedY = newSpeedY;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Particle particle = (Particle) o;
        return Double.compare(particle.mass, mass) == 0 &&
                Double.compare(particle.speedX, speedX) == 0 &&
                Double.compare(particle.speedY, speedY) == 0 &&
                Double.compare(particle.x, x) == 0 &&
                Double.compare(particle.y, y) == 0 &&
                Objects.equals(impacting, particle.impacting);
    }
}
