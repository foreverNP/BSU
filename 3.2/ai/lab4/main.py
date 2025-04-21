import pandas as pd
from sklearn.cluster import KMeans
from sklearn.preprocessing import StandardScaler, OneHotEncoder
from sklearn.metrics import silhouette_score
from sklearn.decomposition import PCA
from sklearn.pipeline import Pipeline
from sklearn.compose import ColumnTransformer
import tkinter as tk
from tkinter import ttk, messagebox, scrolledtext
import matplotlib.pyplot as plt
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg
from sklearn.feature_extraction.text import TfidfVectorizer
import re

data = None
file_path = None
selected_features = []
clusters_result = None
fig = None
canvas = None


def load_data():
    global data, file_path

    try:
        file_path = "wine_reviews.csv"
        data = pd.read_csv(file_path)

        data = data.drop_duplicates()

        data["price"] = data["price"].fillna(data["price"].median())
        data["description"] = data["description"].fillna("")

        update_data_preview()

        update_feature_selection()

        messagebox.showinfo("Success", f"Wine reviews dataset loaded successfully!")
    except Exception as e:
        messagebox.showerror("Error", f"Failed to load dataset: {e}")


def update_data_preview():
    if data is not None:
        preview_text.config(state=tk.NORMAL)
        preview_text.delete(1.0, tk.END)
        preview_text.insert(tk.END, data.head().to_string())
        preview_text.config(state=tk.DISABLED)


def update_feature_selection():
    global feature_vars

    for widget in features_frame.winfo_children():
        widget.destroy()

    if data is not None:
        canvas = tk.Canvas(features_frame)
        scrollbar = ttk.Scrollbar(features_frame, orient="vertical", command=canvas.yview)
        scrollable_frame = ttk.Frame(canvas)

        scrollable_frame.bind("<Configure>", lambda e: canvas.configure(scrollregion=canvas.bbox("all")))

        canvas.create_window((0, 0), window=scrollable_frame, anchor="nw")
        canvas.configure(yscrollcommand=scrollbar.set)

        canvas.pack(side="left", fill="both", expand=True)
        scrollbar.pack(side="right", fill="y")

        feature_vars = {}
        numeric_cols = data.select_dtypes(include=["number"]).columns.tolist()
        categorical_cols = data.select_dtypes(include=["object"]).columns.tolist()

        ttk.Label(scrollable_frame, text="Numeric Features", font=("Arial", 10, "bold")).pack(anchor="w")
        for col in numeric_cols:
            if col != "Unnamed: 0":
                var = tk.BooleanVar(value=False)
                feature_vars[col] = var
                ttk.Checkbutton(scrollable_frame, text=col, variable=var).pack(anchor="w")

        ttk.Label(scrollable_frame, text="Categorical Features", font=("Arial", 10, "bold")).pack(anchor="w")
        for col in categorical_cols:
            if col not in ["description", "title", "taster_name", "taster_twitter_handle"]:
                var = tk.BooleanVar(value=False)
                feature_vars[col] = var
                ttk.Checkbutton(scrollable_frame, text=col, variable=var).pack(anchor="w")

        ttk.Label(scrollable_frame, text="Text Features", font=("Arial", 10, "bold")).pack(anchor="w")
        var = tk.BooleanVar(value=False)
        feature_vars["use_description"] = var
        ttk.Checkbutton(scrollable_frame, text="Use wine descriptions (TF-IDF)", variable=var).pack(anchor="w")


# Function to get selected features
def get_selected_features():
    global selected_features

    if feature_vars:
        selected_features = []
        for feature, var in feature_vars.items():
            if var.get():
                selected_features.append(feature)

    return selected_features


# Function to run clustering
def run_clustering():
    global data, clusters_result

    if data is None:
        messagebox.showerror("Error", "Please load the dataset first!")
        return

    try:
        n_clusters = int(k_entry.get())
        if n_clusters < 2 or n_clusters > 10:
            messagebox.showerror("Error", "Please enter a valid number of clusters (2-10).")
            return
    except ValueError:
        messagebox.showerror("Error", "Please enter a valid positive integer for the number of clusters.")
        return

    selected_features = get_selected_features()

    if not selected_features:
        messagebox.showerror("Error", "Please select at least one feature for clustering.")
        return

    # Create a copy of the data for processing
    analysis_data = data.copy()

    # Handle special text feature
    use_description = False
    if "use_description" in selected_features:
        use_description = True
        selected_features.remove("use_description")

    # Get numeric and categorical features
    numeric_features = []
    categorical_features = []

    for feature in selected_features:
        if feature in data.select_dtypes(include=["number"]).columns:
            numeric_features.append(feature)
        else:
            categorical_features.append(feature)

    # Create preprocessing pipeline
    preprocessor_steps = []

    if numeric_features:
        numeric_transformer = Pipeline(steps=[("scaler", StandardScaler())])
        preprocessor_steps.append(("num", numeric_transformer, numeric_features))

    if categorical_features:
        categorical_transformer = Pipeline(steps=[("onehot", OneHotEncoder(handle_unknown="ignore", sparse_output=False))])
        preprocessor_steps.append(("cat", categorical_transformer, categorical_features))

    # Add TF-IDF for description if selected
    if use_description:
        # Clean and preprocess text
        analysis_data["clean_description"] = analysis_data["description"].apply(lambda x: re.sub(r"[^\w\s]", "", str(x).lower()))

        text_transformer = Pipeline(steps=[("tfidf", TfidfVectorizer(max_features=100, stop_words="english"))])

        preprocessor_steps.append(("text", text_transformer, "clean_description"))

    # Create the column transformer
    preprocessor = ColumnTransformer(transformers=preprocessor_steps, remainder="drop")

    # Apply preprocessing
    X = preprocessor.fit_transform(analysis_data)

    # Run KMeans
    kmeans = KMeans(n_clusters=n_clusters, random_state=42, n_init=10)
    cluster_labels = kmeans.fit_predict(X)

    # Add cluster labels to the original data
    analysis_data["cluster"] = cluster_labels
    clusters_result = analysis_data

    # Calculate silhouette score if we have more than one cluster
    if n_clusters > 1:
        silhouette_avg = silhouette_score(X, cluster_labels)
        status_text.config(state=tk.NORMAL)
        status_text.delete(1.0, tk.END)
        status_text.insert(tk.END, f"Clustering completed.\nNumber of clusters: {n_clusters}\nSilhouette Score: {silhouette_avg:.3f}")
        if silhouette_avg < 0.2:
            status_text.insert(tk.END, "\nNote: Low silhouette score indicates poor cluster separation.")
        status_text.config(state=tk.DISABLED)
    else:
        status_text.config(state=tk.NORMAL)
        status_text.delete(1.0, tk.END)
        status_text.insert(tk.END, f"Clustering completed.\nNumber of clusters: {n_clusters}")
        status_text.config(state=tk.DISABLED)

    # Visualize results
    visualize_clusters(X, cluster_labels, selected_features, use_description)

    # Display cluster statistics
    display_cluster_stats(analysis_data)


def visualize_clusters(X, labels, features, text_used):
    global fig, canvas

    if fig:
        plt.close(fig)

    fig = plt.figure(figsize=(10, 8))

    ax1 = fig.add_subplot(2, 1, 1)

    pca = PCA(n_components=2)
    X_pca = pca.fit_transform(X)

    scatter = ax1.scatter(X_pca[:, 0], X_pca[:, 1], c=labels, cmap="viridis", alpha=0.7, edgecolors="w")
    ax1.set_title("PCA Visualization of Wine Clusters")
    ax1.set_xlabel("Principal Component 1")
    ax1.set_ylabel("Principal Component 2")
    ax1.grid(True, alpha=0.3)

    legend = ax1.legend(*scatter.legend_elements(), title="Clusters")
    ax1.add_artist(legend)

    ax2 = fig.add_subplot(2, 1, 2)

    cluster_counts = pd.Series(labels).value_counts().sort_index()
    bars = ax2.bar(cluster_counts.index, cluster_counts.values, alpha=0.7)

    cmap = plt.cm.viridis
    for i, bar in enumerate(bars):
        bar.set_color(cmap(i / len(bars)))

    ax2.set_title("Distribution of Wines in Each Cluster")
    ax2.set_xlabel("Cluster")
    ax2.set_ylabel("Number of Wines")
    ax2.grid(True, alpha=0.3, axis="y")

    for bar in bars:
        height = bar.get_height()
        ax2.text(bar.get_x() + bar.get_width() / 2.0, height + 5, f"{int(height)}", ha="center", va="bottom")

    plt.tight_layout()

    if canvas:
        canvas.get_tk_widget().destroy()

    canvas = FigureCanvasTkAgg(fig, master=plot_frame)
    canvas.draw()
    canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True)

    notebook.select(1)


def display_cluster_stats(clustered_data):
    if clustered_data is None:
        return

    stats_text.config(state=tk.NORMAL)
    stats_text.delete(1.0, tk.END)

    stats_text.insert(tk.END, "CLUSTER STATISTICS\n\n", "heading")

    # Configure tags for better formatting
    stats_text.tag_configure("heading", font=("Arial", 12, "bold"))
    stats_text.tag_configure("cluster_heading", font=("Arial", 11, "bold"))
    stats_text.tag_configure("normal", font=("Arial", 10))

    for cluster in sorted(clustered_data["cluster"].unique()):
        cluster_data = clustered_data[clustered_data["cluster"] == cluster]
        stats_text.insert(tk.END, f"Cluster {cluster} ({len(cluster_data)} wines):\n", "cluster_heading")

        # Most common countries
        if "country" in clustered_data.columns:
            top_countries = cluster_data["country"].value_counts().head(3)
            stats_text.insert(tk.END, "  Top countries: ", "normal")
            for i, (country, count) in enumerate(top_countries.items()):
                stats_text.insert(tk.END, f"{country} ({count})", "normal")
                if i < len(top_countries) - 1:
                    stats_text.insert(tk.END, ", ", "normal")
            stats_text.insert(tk.END, "\n", "normal")

        # Most common varieties
        if "variety" in clustered_data.columns:
            top_varieties = cluster_data["variety"].value_counts().head(3)
            stats_text.insert(tk.END, "  Top varieties: ", "normal")
            for i, (variety, count) in enumerate(top_varieties.items()):
                stats_text.insert(tk.END, f"{variety} ({count})", "normal")
                if i < len(top_varieties) - 1:
                    stats_text.insert(tk.END, ", ", "normal")
            stats_text.insert(tk.END, "\n", "normal")

        # Average price and points if available
        if "price" in clustered_data.columns:
            avg_price = cluster_data["price"].mean()
            stats_text.insert(tk.END, f"  Avg. price: ${avg_price:.2f}\n", "normal")

        if "points" in clustered_data.columns:
            avg_points = cluster_data["points"].mean()
            stats_text.insert(tk.END, f"  Avg. points: {avg_points:.1f}\n", "normal")

        stats_text.insert(tk.END, "\n", "normal")

    stats_text.config(state=tk.DISABLED)


root = tk.Tk()
root.title("Wine Reviews Clustering Tool")
root.geometry("1200x800")
root.configure(bg="#f5f5f5")

style = ttk.Style()
style.theme_use("clam")
style.configure("TButton", font=("Arial", 10), background="#4CAF50")
style.configure("TLabel", font=("Arial", 10), background="#f5f5f5")
style.configure("TFrame", background="#f5f5f5")

# Create main frames
header_frame = ttk.Frame(root, padding=10)
header_frame.pack(fill=tk.X)

content_frame = ttk.Frame(root)
content_frame.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)

# Left panel for controls
left_panel = ttk.Frame(content_frame, width=300)
left_panel.pack(side=tk.LEFT, fill=tk.BOTH, padx=(0, 5))

# Right panel for visualization
right_panel = ttk.Frame(content_frame)
right_panel.pack(side=tk.RIGHT, fill=tk.BOTH, expand=True, padx=(5, 0))

# Add a notebook to separate visualizations and statistics
notebook = ttk.Notebook(right_panel)
notebook.pack(fill=tk.BOTH, expand=True)

# Plot frame as a tab
plot_frame = ttk.Frame(notebook)
notebook.add(plot_frame, text="Cluster Visualization")

# Cluster statistics as a tab
stats_frame = ttk.Frame(notebook)
notebook.add(stats_frame, text="Cluster Statistics")

# Stats text in its own frame with more height
stats_text = scrolledtext.ScrolledText(stats_frame, wrap=tk.WORD)
stats_text.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)
stats_text.config(state=tk.DISABLED)

# Header with title
ttk.Label(header_frame, text="Wine Reviews Clustering Analysis", font=("Arial", 16, "bold")).pack()

# Add a data preview section
preview_frame = ttk.LabelFrame(left_panel, text="Data Preview", padding=5)
preview_frame.pack(fill=tk.BOTH, expand=True, pady=(0, 10))

preview_text = scrolledtext.ScrolledText(preview_frame, height=8, width=40, wrap=tk.WORD)
preview_text.pack(fill=tk.BOTH, expand=True)
preview_text.config(state=tk.DISABLED)

# Create feature selection frame
feature_frame = ttk.LabelFrame(left_panel, text="Select Features for Clustering", padding=5)
feature_frame.pack(fill=tk.BOTH, expand=True, pady=(0, 10))

features_frame = ttk.Frame(feature_frame)
features_frame.pack(fill=tk.BOTH, expand=True)

# Clustering parameters
params_frame = ttk.LabelFrame(left_panel, text="Clustering Parameters", padding=5)
params_frame.pack(fill=tk.X, pady=(0, 10))

ttk.Label(params_frame, text="Number of clusters (K):").pack(anchor="w", pady=(5, 0))
k_entry = ttk.Entry(params_frame, width=10)
k_entry.pack(anchor="w", pady=(0, 5))
k_entry.insert(0, "3")

# Buttons frame
buttons_frame = ttk.Frame(left_panel)
buttons_frame.pack(fill=tk.X, pady=(0, 10))

load_button = ttk.Button(buttons_frame, text="Load Wine Data", command=load_data)
load_button.pack(side=tk.LEFT, padx=(0, 5), fill=tk.X, expand=True)

cluster_button = ttk.Button(buttons_frame, text="Run Clustering", command=run_clustering)
cluster_button.pack(side=tk.RIGHT, fill=tk.X, expand=True)

# Status text
status_frame = ttk.LabelFrame(left_panel, text="Status", padding=5)
status_frame.pack(fill=tk.X)

status_text = scrolledtext.ScrolledText(status_frame, height=4, width=40, wrap=tk.WORD)
status_text.pack(fill=tk.BOTH, expand=True)
status_text.config(state=tk.DISABLED)

root.mainloop()
