package com.phi.material.ffmpeg;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FfmpegService {

    @Value("${phi.ffmpeg.cmd-path}")
    private String ffmpeg;

    @SuppressWarnings("preview")
    private String[] cmd(Path input, Path output) {
        return new String[]{
                ffmpeg,
                "-i", STR."\{input.toAbsolutePath()}",
                // 720p
                "-filter:v", "scale=1280:-1", "-g", "30",
                "-profile:v", "main", "-level", "4.0",
                "-c:v", "libx264",
                "-b:v", "1500k", "-maxrate", "1500k", "-bufsize", "2250k",
                "-start_number", "0", "-hls_time", "1", "-hls_list_size", "0",
                "-f", "hls",
                STR."\{output.toAbsolutePath()}/720p/slice.m3u8",
                // 1080p
                "-filter:v", "scale=1280:-1",
                "-g", "30",
                "-profile:v", "high", "-level", "4.2",
                "-c:v", "libx264",
                "-b:v", "3000k", "-maxrate", "3000k", "-bufsize", "4500k",
                "-start_number", "0", "-hls_time", "1", "-hls_list_size", "0",
                "-f", "hls",
                STR."\{output.toAbsolutePath()}/1080p/slice.m3u8"
        };
    }

    public void slice(Path input, Path output, Consumer<String> consumer) {
        try {
            Files.createDirectories(output.resolve("1080p"));
            Files.createDirectories(output.resolve("720p"));
            execute(consumer, cmd(input, output));
            Files.writeString(output.resolve("slice.m3u8"), """
                    #EXTM3U
                    #EXT-X-STREAM-INF:BANDWIDTH=1500000,RESOLUTION=1280x720
                    720p/slice.m3u8
                    #EXT-X-STREAM-INF:BANDWIDTH=3000000,RESOLUTION=1920x1080
                    1080p/slice.m3u8
                    """, TRUNCATE_EXISTING, CREATE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("preview")
    public void thumbnail(Path input, Path output) {
        String value = execute("ffprobe",
                "-v", "error",
                "-show_entries", "format=duration",
                "-of", "default=noprint_wrappers=1",
                STR."\{input.toAbsolutePath()}");
        if (!value.startsWith("duration=")) {
            throw new RuntimeException(STR."thumbnail failed:\{value}");
        }
        int total = (int) Double.parseDouble(value.substring("duration=".length()));
        int time = ThreadLocalRandom.current().nextInt(total / 2, total);
        execute(ffmpeg,
                "-i", STR."\{input.toAbsolutePath()}",
                "-ss", STR."\{time}",
                "-frames:v", "1",
                STR."\{output.toAbsolutePath()}");
    }

    @SuppressWarnings("preview")
    private static void execute(Consumer<String> consumer, String... cmds) {
        Process process = null;
        try {
            process = new ProcessBuilder()
                    .command(cmds)
                    .redirectErrorStream(true)
                    .start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                consumer.accept(line);
            }

            String value = new String(process.getInputStream().readAllBytes());
            process.waitFor();
            if (0 != process.waitFor()) {
                throw new RuntimeException(STR."get failed: \{value}");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    @SuppressWarnings("preview")
    private static String execute(String... cmds) {
        Process process = null;
        try {
            process = new ProcessBuilder()
                    .command(cmds)
                    .redirectErrorStream(true)
                    .start();
            String value = new String(process.getInputStream().readAllBytes());
            process.waitFor();
            if (0 != process.waitFor()) {
                throw new RuntimeException(STR."get failed: \{value}");
            } else {
                return value;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
}
