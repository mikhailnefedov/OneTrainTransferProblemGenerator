import io

from flask import Flask, request
import numpy as np

import prelim
from prelim import bound_outliers, normalize, get_bounds_and_bound_outliers, get_normalization_lambdas
from visualization_storage import VisualizationStorage

app = Flask(__name__)

visualization_storage = VisualizationStorage()


def create_feature_vector(instances, feature_names):
    instances_and_feature_vectors = []
    for instance in instances:
        feature_values = []
        for feature_name in feature_names:
            feature_values.append(instance["featureDescription"][feature_name])
        instances_and_feature_vectors.append((instance, np.array(feature_values)))
    return instances_and_feature_vectors


def preprocess_feature_vectors(feature_vectors):
    unprocessed_feature_vectors = np.array(feature_vectors)
    bounded_feature_vectors = bound_outliers(unprocessed_feature_vectors)
    return normalize(bounded_feature_vectors)


def preprocess_instances_and_feature_vectors(instances_and_feature_vectors):
    normalized_feature_vectors = preprocess_feature_vectors([x[1] for x in instances_and_feature_vectors])
    for i in range(0, len(instances_and_feature_vectors)):
        instances_and_feature_vectors[i] = (instances_and_feature_vectors[i][0], normalized_feature_vectors[i])


def project_instances(transposed_projection_matrix, instance_feature_vector_pairs):
    projection_matrix = np.transpose(np.array(transposed_projection_matrix))
    projected_instance_feature_vector_pairs = []
    for pair in instance_feature_vector_pairs:
        transposed_feature_vector = np.array(np.transpose(pair[1]))
        coordinates = np.matmul(projection_matrix, transposed_feature_vector)
        projected_instance_feature_vector_pairs.append((pair[0], coordinates))
    return projected_instance_feature_vector_pairs


@app.route('/visualizationbysource', methods=['POST'])
def visualization_by_source():
    json_data = request.json
    instances_and_feature_vectors = create_feature_vector(json_data["instances"], json_data["featureNames"])
    preprocess_instances_and_feature_vectors(instances_and_feature_vectors)
    instance_and_coord_pairs = project_instances(json_data["transposedProjectionMatrix"], instances_and_feature_vectors)

    visualization_storage.set_plot_dimensions(json_data["axisRangeX"], json_data["axisRangeY"])
    visualization_storage.add_visualization_by_source(instance_and_coord_pairs)
    return "Ok"


@app.route('/visualizationbystationcount', methods=['POST'])
def visualization_by_station_count():
    json_data = request.json
    instances_and_feature_vectors = create_feature_vector(json_data["instances"], json_data["featureNames"])
    preprocess_instances_and_feature_vectors(instances_and_feature_vectors)
    instance_and_coord_pairs = project_instances(json_data["transposedProjectionMatrix"], instances_and_feature_vectors)

    visualization_storage.set_plot_dimensions(json_data["axisRangeX"], json_data["axisRangeY"])
    visualization_storage.add_visualization_by_station_count(instance_and_coord_pairs, json_data["stationCount"])
    return "Ok"


@app.route('/showvisualizations')
def show_visualizations():
    plots = visualization_storage.get_plots()
    html = ""
    for plot in plots:
        img = io.StringIO()
        plot.savefig(img, format="svg")
        html += '<svg' + img.getvalue().split('<svg')[1]
    return html


@app.route('/instancecoords', methods=['POST'])
def get_instance_coords():
    json_data = request.json
    instances_and_feature_vectors = create_feature_vector(json_data["instances"], json_data["featureNames"])
    preprocess_instances_and_feature_vectors(instances_and_feature_vectors)
    instance_and_coord_pairs = project_instances(json_data["transposedProjectionMatrix"], instances_and_feature_vectors)

    coords = [pair[1].tolist() for pair in instance_and_coord_pairs]
    return {"coordinates": coords}


@app.route('/outliernormalizedata', methods=['POST'])
def get_outlier_normalize_data():
    json_data = request.json
    instances_and_feature_vectors = create_feature_vector(json_data["instances"], json_data["featureNames"])
    unprocessed_feature_vectors = np.array([pair[1] for pair in instances_and_feature_vectors])
    column_min, column_max, feature_min, bounded_feature_vectors = get_bounds_and_bound_outliers(
        unprocessed_feature_vectors)
    lambdas = get_normalization_lambdas(bounded_feature_vectors)

    data = []
    for i in range(0, len(json_data["featureNames"])):
        feature_data = {
            "columnMin": column_min[i],
            "columnMax": column_max[i],
            "featureMin": feature_min[i],
            "lambda": lambdas[i]
        }
        data.append(feature_data)
    return {"featureData": data}


if __name__ == '__main__':
    app.run()
