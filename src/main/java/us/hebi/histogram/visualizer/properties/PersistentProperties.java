package us.hebi.histogram.visualizer.properties;

import javafx.beans.property.*;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Stores properties via Preferences API. Previous values are loaded
 * at initialization, and stored on close.
 * <p>
 * Preferences are stored by user and by package name. Classes in the
 * same package must have unique strings.
 *
 * @author Florian Enner
 * @since 02 Aug 2021
 */
public class PersistentProperties {

    public BooleanProperty getBoolean(String name, boolean defValue) {
        var prop = new SimpleBooleanProperty(prefs.getBoolean(name, defValue));
        onSave.add(() -> prefs.putBoolean(name, prop.getValue()));
        onReset.add(() -> prop.set(defValue));
        return prop;
    }

    public FloatProperty getFloat(String name, float defValue) {
        var prop = new SimpleFloatProperty(prefs.getFloat(name, defValue));
        onReset.add(() -> prop.set(defValue));
        onSave.add(() -> prefs.putFloat(name, prop.getValue()));
        return prop;
    }

    public DoubleProperty getDouble(String name, double defValue) {
        var prop = new SimpleDoubleProperty(prefs.getDouble(name, defValue));
        onReset.add(() -> prop.set(defValue));
        onSave.add(() -> prefs.putDouble(name, prop.getValue()));
        return prop;
    }

    public IntegerProperty getInt(String name, int defValue) {
        var prop = new SimpleIntegerProperty(prefs.getInt(name, defValue));
        onReset.add(() -> prop.set(defValue));
        onSave.add(() -> prefs.putInt(name, prop.getValue()));
        return prop;
    }

    public LongProperty getLong(String name, long defValue) {
        var prop = new SimpleLongProperty(prefs.getLong(name, defValue));
        onReset.add(() -> prop.set(defValue));
        onSave.add(() -> prefs.putLong(name, prop.getValue()));
        return prop;
    }

    public StringProperty getString(String name) {
        return getString(name, "");
    }

    public StringProperty getString(String name, String defValue) {
        var prop = new SimpleStringProperty(prefs.get(name, defValue));
        onReset.add(() -> prop.set(defValue));
        onSave.add(() -> prefs.put(name, prop.getValue()));
        return prop;
    }

    @SuppressWarnings("unchecked")
    public <E extends Enum<E>> ObjectProperty<E> getEnum(String name, E defValue) {
        E value;
        try {
            value = (E) Enum.valueOf(defValue.getClass(), prefs.get(name, defValue.name()));
        } catch (NullPointerException | IllegalArgumentException notFound) {
            value = defValue;
        }
        var prop = new SimpleObjectProperty<E>(value);
        onReset.add(() -> prop.set(defValue));
        onSave.add(() -> prefs.put(name, Optional.ofNullable(prop.get())
                .map(Enum::name)
                .orElse("")));
        return prop;
    }

    /**
     * resets all values to their defaults
     */
    public void reset() {
        onReset.forEach(Runnable::run);
    }

    /**
     * Clears existing user preferences from the store
     */
    public void clear() throws BackingStoreException {
        prefs.clear();
    }

    @PreDestroy
    public void save() {
        onSave.forEach(Runnable::run);
    }

    public PersistentProperties() {
        prefs = Preferences.userNodeForPackage(this.getClass());
    }

    public PersistentProperties(Class<?> clazz) {
        prefs = Preferences.userNodeForPackage(Objects.requireNonNull(clazz));
    }

    public PersistentProperties(Preferences prefs) {
        this.prefs = Objects.requireNonNull(prefs);
    }

    final List<Runnable> onReset = new ArrayList<>(4);
    final List<Runnable> onSave = new ArrayList<>(4);
    final Preferences prefs;

}
