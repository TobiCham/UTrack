package edu.utrack.goals;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import edu.utrack.goals.active.ActiveGoal;
import edu.utrack.goals.active.ActiveObjective;
import edu.utrack.goals.archived.ArchivedGoal;
import edu.utrack.goals.archived.ArchivedObjective;

public class GoalManager {

    private static final int FORMAT = 0;

    private File saveFile;

    private ActiveGoal dailyGoal;
    private ActiveGoal weeklyGoal;

    private int trophies = 0;

    private List<ArchivedGoal> archivedGoals = new ArrayList<>();

    public GoalManager(File file) {
        this.saveFile = file;
        load();
    }

    public ActiveGoal getGoal(GoalType type) {
        if(type == null) return null;
        if(type == GoalType.DAILY) return getDailyGoal();
        else return getWeeklyGoal();
    }
    
    public ActiveGoal getDailyGoal() {
		return dailyGoal;
	}
    
    public ActiveGoal getWeeklyGoal() {
		return weeklyGoal;
	}

    public int getTrophies() {
        return trophies;
    }

    public void setTrophies(int trophies) {
        this.trophies = trophies;
        save();
    }

    public List<ArchivedGoal> getArchivedGoals() {
        return Collections.unmodifiableList(archivedGoals);
    }

    public void addArchivedGoal(ArchivedGoal goal) {
    	this.archivedGoals.add(goal);
    	save();
    }
    
    public void addArchivedGoals(ArchivedGoal...goals) {
    	for(ArchivedGoal goal : goals) archivedGoals.add(goal);
    	save();
    }

    private void load() {
        if(!saveFile.exists()) {
            dailyGoal = new ActiveGoal(GoalType.DAILY);
            weeklyGoal = new ActiveGoal(GoalType.WEEKLY);

            //Enable to add random objectives/goals to the archived list
//            Random rand = new Random();
//            for(int i = 0; i < 15; i++) {
//
//                List<ArchivedObjective> objectives = new ArrayList<>();
//                for(int j = 0; j < rand.nextInt(5) + 2; j++) {
//                    objectives.add(new ArchivedObjective((i + j) + "", ObjectiveType.fromId(rand.nextInt(2)), ObjectiveValueType.fromId(rand.nextInt(3)), rand.nextDouble(), rand.nextInt(3) - 1));
//                }
//
//                GoalActivityData data = new GoalActivityData(rand.nextInt(5000), rand.nextInt(60), rand.nextInt(150), rand.nextBoolean() ? 0 : rand.nextInt(10000));
//                archivedGoals.add(new ArchivedGoal(rand.nextBoolean() ? GoalType.DAILY : GoalType.WEEKLY, objectives, rand.nextLong(), rand.nextLong(), data));
//            }

            save();
            return;
        }

        try(FileInputStream fin = new FileInputStream(saveFile); GZIPInputStream gzip = new GZIPInputStream(fin); DataInputStream din = new DataInputStream(gzip)) {
        	int version = din.readInt();

        	trophies = din.readInt();

            dailyGoal = (ActiveGoal) readGoal(din, true, version);
            weeklyGoal = (ActiveGoal) readGoal(din, true, version);

            int archivedSize = din.readInt();
            if(archivedSize < 0) throw new IOException("Invalid archived goals size");

            archivedGoals.clear();
            for(int i = 0; i < archivedSize; i++) {
            	archivedGoals.add((ArchivedGoal) readGoal(din, false, version));
            }
            
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try(FileOutputStream fout = new FileOutputStream(saveFile); GZIPOutputStream gzip = new GZIPOutputStream(fout); DataOutputStream dout = new DataOutputStream(gzip)) {

            dout.writeInt(FORMAT);

            dout.writeInt(trophies);

            writeGoal(dailyGoal, dout);
            writeGoal(weeklyGoal, dout);

            dout.writeInt(archivedGoals.size());
            for(ArchivedGoal archived : archivedGoals) {
                writeGoal(archived, dout);
            }
            dout.flush();

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void writeGoal(Goal<?> goal, DataOutputStream out) throws IOException {
        out.writeInt(goal.getType().getId());

        out.writeInt(goal.getObjectives().size());
        for(Objective objective : goal.getObjectives()) {
            writeObjective(objective, out);
        }

        if(goal instanceof ArchivedGoal) {
            ArchivedGoal archivedGoal = (ArchivedGoal) goal;
            out.writeLong(archivedGoal.getBeginTime());
            out.writeLong(archivedGoal.getEndTime());
            writeActivityData(archivedGoal.getActivityData(), out);
        }
    }

    private void writeActivityData(GoalActivityData data, DataOutputStream out) throws IOException {
        out.writeInt(data.getTotalAppTime());
        out.writeInt(data.getUniqueApps());
        out.writeInt(data.getScreenOns());
        out.writeInt(data.getTotalEventTime());
    }

    private Goal<?> readGoal(DataInputStream in, boolean active, int version) throws IOException {
        GoalType type = GoalType.fromId(in.readInt());
        if(type == null) throw new IOException("Invalid goal type");

        int objectiveSize = in.readInt();
        if(objectiveSize < 0) throw new IOException("Invalid objective size");
        
        if(active) {
            ActiveGoal goal = new ActiveGoal(type);
            for(int i = 0; i < objectiveSize; i++) goal.addObjective((ActiveObjective) readObjective(in, true, version));
            return goal;
        }
        List<ArchivedObjective> objectives = new ArrayList<>(objectiveSize);
        for(int i = 0; i < objectiveSize; i++) objectives.add((ArchivedObjective) readObjective(in, false, version));

        long begin = in.readLong();
        long end = in.readLong();

        return new ArchivedGoal(type, objectives, begin, end, readActivityData(in));
    }

    private GoalActivityData readActivityData(DataInputStream in) throws IOException {
        return new GoalActivityData(in.readInt(), in.readInt(), in.readInt(), in.readInt());
    }

    private void writeObjective(Objective objective, DataOutputStream out) throws IOException {
        out.writeUTF(objective.getDescription());
        out.writeInt(objective.getType().getId());
        out.writeInt(objective.getValueType().getId());
        writeNumber(objective.getValueType(), objective.getValue(), out);

        if(objective instanceof ArchivedObjective) out.writeInt(((ArchivedObjective) objective).getCompletedState());
    }

    private Objective readObjective(DataInputStream in, boolean active, int version) throws IOException {
        String name = in.readUTF();

        ObjectiveType type = ObjectiveType.fromId(in.readInt());
        if(type == null) throw new IOException("Invalid objective type");

        ObjectiveValueType valueType = ObjectiveValueType.fromId(in.readInt());
        Number value = readNumber(valueType, in);

        if(active) return new ActiveObjective(name, type, valueType, value);

        int complete = in.readInt();
        return new ArchivedObjective(name, type, valueType, value, complete);
    }

    private void writeNumber(ObjectiveValueType type, Number number, DataOutputStream out) throws IOException {
        Class<? extends Number> clas = type.getDataType();
        if(clas == byte.class || clas == Byte.class) out.write(number.byteValue());
        if(clas == short.class || clas == Short.class) out.writeShort(number.shortValue());
        if(clas == int.class || clas == Integer.class) out.writeInt(number.intValue());
        if(clas == long.class || clas == Long.class) out.writeLong(number.longValue());
        if(clas == float.class || clas == Float.class) out.writeFloat(number.floatValue());
        if(clas == double.class || clas == Double.class) out.writeDouble(number.doubleValue());
    }

    private Number readNumber(ObjectiveValueType type, DataInputStream in) throws IOException {
        Class<? extends Number> clas = type.getDataType();
        if(clas == byte.class || clas == Byte.class) return in.readByte();
        if(clas == short.class || clas == Short.class) return in.readShort();
        if(clas == int.class || clas == Integer.class) return in.readInt();
        if(clas == long.class || clas == Long.class) return in.readLong();
        if(clas == float.class || clas == Float.class) return in.readFloat();
        if(clas == double.class || clas == Double.class) return in.readDouble();
        throw new IOException("Invalid number type " + type);
    }
}