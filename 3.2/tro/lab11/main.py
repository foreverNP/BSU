import tkinter as tk
from tkinter import filedialog, messagebox
from PIL import Image, ImageTk
import cv2
import numpy as np
import os


class CropLossAnalyzer:
    def __init__(self, root):
        self.root = root
        self.root.title("Анализатор потерь урожая")
        self.root.geometry("800x600")

        # Параметры хозяйства
        self.total_area = 190  # га
        self.yield_per_ha = 54  # центнеров/га

        # Переменные для хранения изображений
        self.image_path = None
        self.original_image = None
        self.cv_image = None
        self.processed_image = None

        # Основная рамка
        main_frame = tk.Frame(root)
        main_frame.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)

        # Рамка для изображений (исходное и обработанное)
        images_frame = tk.Frame(main_frame)
        images_frame.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)

        # Фиксированный размер для рамок с изображениями
        frame_width, frame_height = 380, 300

        # Рамка для исходного изображения
        self.image_frame = tk.LabelFrame(images_frame, text="Исходное изображение", width=frame_width, height=frame_height)
        self.image_frame.grid(row=0, column=0, padx=5, pady=5, sticky="nsew")
        self.image_frame.grid_propagate(False)

        # Метка для отображения исходного изображения
        self.image_label = tk.Label(self.image_frame)
        self.image_label.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)

        # Рамка для обработанного изображения
        self.processed_frame = tk.LabelFrame(images_frame, text="Результат анализа", width=frame_width, height=frame_height)
        self.processed_frame.grid(row=0, column=1, padx=5, pady=5, sticky="nsew")
        self.processed_frame.grid_propagate(False)

        # Метка для отображения обработанного изображения
        self.processed_label = tk.Label(self.processed_frame)
        self.processed_label.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)

        # Настройка сетки для равномерного распределения
        images_frame.grid_columnconfigure(0, weight=1)
        images_frame.grid_columnconfigure(1, weight=1)
        images_frame.grid_rowconfigure(0, weight=1)

        control_frame = tk.Frame(main_frame)
        control_frame.pack(fill=tk.X, padx=5, pady=5)

        self.load_button = tk.Button(control_frame, text="Загрузить изображение", command=self.load_image)
        self.load_button.pack(side=tk.LEFT, padx=5)

        self.analyze_button = tk.Button(control_frame, text="Анализировать потери", command=self.analyze_losses)
        self.analyze_button.pack(side=tk.LEFT, padx=5)

        settings_frame = tk.Frame(main_frame, bd=1, relief=tk.RIDGE)
        settings_frame.pack(fill=tk.X, padx=5, pady=5)

        tk.Label(settings_frame, text="Общая площадь (га):").grid(row=0, column=0, sticky=tk.W, padx=5, pady=5)
        self.area_entry = tk.Entry(settings_frame)
        self.area_entry.insert(0, str(self.total_area))
        self.area_entry.grid(row=0, column=1, padx=5, pady=5)

        tk.Label(settings_frame, text="Урожайность (ц/га):").grid(row=1, column=0, sticky=tk.W, padx=5, pady=5)
        self.yield_entry = tk.Entry(settings_frame)
        self.yield_entry.insert(0, str(self.yield_per_ha))
        self.yield_entry.grid(row=1, column=1, padx=5, pady=5)

        self.results_frame = tk.Frame(main_frame, bd=1, relief=tk.RIDGE)
        self.results_frame.pack(fill=tk.X, padx=5, pady=5)

        tk.Label(self.results_frame, text="Результаты анализа", font=("Arial", 12, "bold")).pack(pady=5)

        self.results_text = tk.Text(self.results_frame, height=6, width=60)
        self.results_text.pack(padx=5, pady=5, fill=tk.X)

    def load_image(self):
        """Загрузка изображения"""
        file_path = filedialog.askopenfilename(filetypes=[("Image files", "*.png;*.jpg;*.jpeg;*.bmp")])

        if file_path:
            self.image_path = file_path
            try:
                self.original_image = Image.open(file_path)

                # Конвертируем для OpenCV
                self.cv_image = cv2.cvtColor(np.array(self.original_image), cv2.COLOR_RGB2BGR)

                self.display_image(self.original_image, self.image_label)
                self.root.title(f"Анализатор потерь урожая - {os.path.basename(file_path)}")
            except Exception as e:
                messagebox.showerror("Ошибка", f"Не удалось загрузить изображение: {str(e)}")

    def display_image(self, image, label):
        if image:
            container_width = label.winfo_width()
            container_height = label.winfo_height()

            if container_width <= 1:
                container_width = 370
            if container_height <= 1:
                container_height = 280

            img_width, img_height = image.size
            scale = min(container_width / img_width, container_height / img_height)

            new_width = int(img_width * scale)
            new_height = int(img_height * scale)

            resized = image.resize((new_width, new_height), Image.LANCZOS)
            photo = ImageTk.PhotoImage(resized)

            label.config(image=photo)
            label.image = photo

    def analyze_image(self, image):
        # Преобразование в HSV пространство
        hsv = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)

        # Определяем диапазоны для зеленого цвета в HSV
        # Расширенный диапазон для зеленого цвета
        lower_green = np.array([25, 40, 40])
        upper_green = np.array([95, 255, 255])

        # Определяем диапазоны для желтого цвета в HSV
        lower_yellow = np.array([15, 100, 100])
        upper_yellow = np.array([35, 255, 255])

        green_mask = cv2.inRange(hsv, lower_green, upper_green)
        yellow_mask = cv2.inRange(hsv, lower_yellow, upper_yellow)

        kernel = np.ones((5, 5), np.uint8)
        green_mask = cv2.morphologyEx(green_mask, cv2.MORPH_CLOSE, kernel)
        yellow_mask = cv2.morphologyEx(yellow_mask, cv2.MORPH_CLOSE, kernel)

        green_pixels = cv2.countNonZero(green_mask)
        yellow_pixels = cv2.countNonZero(yellow_mask)

        result_image = np.copy(image)

        # Накладываем зеленую и желтую маски с прозрачностью
        result_image[green_mask > 0] = (0, 255, 0)  # Зеленый
        result_image[yellow_mask > 0] = (0, 255, 255)  # Желтый

        return green_pixels, yellow_pixels, result_image

    def analyze_losses(self):
        """Анализ потерь урожая"""
        if self.cv_image is None:
            messagebox.showwarning("Предупреждение", "Сначала загрузите изображение!")
            return

        try:
            self.total_area = float(self.area_entry.get())
            self.yield_per_ha = float(self.yield_entry.get())

            green_pixels, yellow_pixels, result_image = self.analyze_image(self.cv_image)

            field_pixels = green_pixels + yellow_pixels

            if field_pixels == 0:
                height, width = self.cv_image.shape[:2]
                field_pixels = height * width
                green_pixels = field_pixels
                yellow_pixels = 0

                result_image = np.zeros_like(self.cv_image)
                result_image[:] = (0, 255, 0)

            result_pil = Image.fromarray(cv2.cvtColor(result_image, cv2.COLOR_BGR2RGB))
            self.display_image(result_pil, self.processed_label)

            # Расчет процента потерь
            loss_percentage = (yellow_pixels / field_pixels) * 100 if field_pixels > 0 else 0

            # Расчет потерь урожая
            expected_yield = self.total_area * self.yield_per_ha
            actual_yield = expected_yield * (1 - loss_percentage / 100)
            yield_loss = expected_yield - actual_yield

            self.results_text.delete(1.0, tk.END)
            self.results_text.insert(tk.END, f"Анализ изображения: {os.path.basename(self.image_path)}\n")
            self.results_text.insert(tk.END, f"Пиксели поля: {field_pixels}\n")
            self.results_text.insert(tk.END, f"Зеленые пиксели: {green_pixels} ({green_pixels / field_pixels:.2%})\n")
            self.results_text.insert(tk.END, f"Желтые пиксели: {yellow_pixels} ({yellow_pixels / field_pixels:.2%})\n")
            self.results_text.insert(tk.END, f"Процент потерь: {loss_percentage:.2f}%\n")
            self.results_text.insert(tk.END, f"Ожидаемый урожай: {expected_yield:.2f} центнеров\n")
            self.results_text.insert(tk.END, f"Фактический урожай: {actual_yield:.2f} центнеров\n")
            self.results_text.insert(tk.END, f"Потери урожая: {yield_loss:.2f} центнеров")

        except Exception as e:
            messagebox.showerror("Ошибка", f"Ошибка при анализе: {str(e)}")
            import traceback

            traceback.print_exc()


if __name__ == "__main__":
    root = tk.Tk()
    app = CropLossAnalyzer(root)
    root.mainloop()
