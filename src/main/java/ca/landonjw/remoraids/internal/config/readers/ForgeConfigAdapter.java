package ca.landonjw.remoraids.internal.config.readers;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.internal.api.config.ConfigurationAdapter;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import info.pixelmon.repack.ninja.leaping.configurate.ConfigurationNode;
import info.pixelmon.repack.ninja.leaping.configurate.commented.CommentedConfigurationNode;
import info.pixelmon.repack.ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import info.pixelmon.repack.ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ForgeConfigAdapter implements ConfigurationAdapter {

	private final Path path;

	private ConfigurationNode root;
	private ConfigurationLoader<CommentedConfigurationNode> loader;

	public ForgeConfigAdapter(Path path) {
		this.path = path;
		this.createConfigIfMissing();
		this.reload();
	}

	private void createConfigIfMissing() {
		if(!Files.exists(this.path)) {
			try {
				this.createDirectoriesIfNotExists(this.path.getParent());

				try(InputStream is = RemoRaids.getResourceStream("raids/configuration/" + this.path.getFileName().toString())) {
					if(is == null) {
						System.out.println("Failed to locate resource file: " + this.path.getFileName().toString());
						return;
					}
					Files.copy(is, this.path);
				}
			} catch (Exception e) {
				System.out.println("Failed to read config file: " + this.path.getFileName().toString());
				e.printStackTrace();
			}
		}
	}

	private void createDirectoriesIfNotExists(Path path) throws IOException {
		if (Files.exists(path) && (Files.isDirectory(path) || Files.isSymbolicLink(path))) {
			return;
		}

		Files.createDirectories(path);
	}

	private ConfigurationLoader<? extends ConfigurationNode> createLoader(Path path) {
		return (this.loader = HoconConfigurationLoader.builder().setPath(path).build());
	}

	@Override
	public void reload() {
		ConfigurationLoader<? extends ConfigurationNode> loader = createLoader(this.path);
		try {
			this.root = loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private ConfigurationNode resolvePath(String path) {
		if (this.root == null) {
			throw new RuntimeException("Config is not loaded.");
		}

		return this.root.getNode(Splitter.on('.').splitToList(path).toArray());
	}

	private <T> void checkMissing(String path, T def) {
		if(!this.contains(path)) {
			RemoRaids.logger.warn(Lists.newArrayList(String.format("Found missing config option (%s)... Adding it!", path)));
			resolvePath(path).setValue(def);
			try {
				this.loader.save(root);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private <T> void checkMissing(String path, T def, TypeToken<T> type) {
		try {
			if (!this.contains(path)) {
				RemoRaids.logger.warn(Lists.newArrayList(String.format("Found missing config option (%s)... Adding it!", path)));
				resolvePath(path).setValue(type, def);
				this.loader.save(root);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean contains(String path) {
		return !resolvePath(path).isVirtual();
	}

	@Override
	public String getString(String path, String def) {
		this.checkMissing(path, def);
		return resolvePath(path).getString(def);
	}

	@Override
	public int getInteger(String path, int def) {
		this.checkMissing(path, def);
		return resolvePath(path).getInt(def);
	}

	@Override
	public long getLong(String path, long def) {
		this.checkMissing(path, def);
		return resolvePath(path).getLong(def);
	}

	@Override
	public double getDouble(String path, double def) {
		this.checkMissing(path, def);
		return resolvePath(path).getDouble(def);
	}

	@Override
	public boolean getBoolean(String path, boolean def) {
		this.checkMissing(path, def);
		return resolvePath(path).getBoolean(def);
	}

	@Override
	public List<String> getStringList(String path, List<String> def) {
		this.checkMissing(path, def, new TypeToken<List<String>>() {});
		ConfigurationNode node = resolvePath(path);
		if (node.isVirtual()) {
			return def;
		}

		return node.getList(Object::toString);
	}

	@Override
	public List<String> getKeys(String path, List<String> def) {
		this.checkMissing(path, def, new TypeToken<List<String>>() {});
		ConfigurationNode node = resolvePath(path);
		if (node.isVirtual()) {
			return def;
		}

		return node.getChildrenMap().keySet().stream().map(Object::toString).collect(Collectors.toList());
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String> getStringMap(String path, Map<String, String> def) {
		this.checkMissing(path, def, new TypeToken<Map<String, String>>() {});
		ConfigurationNode node = resolvePath(path);
		if (node.isVirtual()) {
			return def;
		}

		Map<String, Object> m = (Map<String, Object>) node.getValue(Collections.emptyMap());
		return m.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().toString()));
	}
}