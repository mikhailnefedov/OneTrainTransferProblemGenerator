import matplotlib.pyplot as plt
from flask import Flask, request
import numpy as np

from prelim import bound_outliers, normalize

app = Flask(__name__)


def get_feature_vectors(request):
    feature_vectors = []
    for instance in request.json:
        feature_vectors.append(instance['right'])
    return np.array(feature_vectors)


def get_normalized_feature_vectors():
    unprocessed_feature_vectors = get_feature_vectors(request)
    bounded_feature_vectors = bound_outliers(unprocessed_feature_vectors)
    return normalize(bounded_feature_vectors)


@app.route('/preprocessing', methods=['POST'])
def preprocessing():
    normalized_feature_vectors = get_normalized_feature_vectors()
    return {"featureVectors": normalized_feature_vectors.tolist()}


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


def show_plot(instance_coordinate_pairs):
    x_coords = [pair[1][0] for pair in instance_coordinate_pairs]
    y_coords = [pair[1][1] for pair in instance_coordinate_pairs]
    plt.scatter(x_coords, y_coords)
    plt.title("Instance Space")
    plt.show()


@app.route('/visualizationbysource', methods=['POST'])
def visualization_by_source():
    json_data = request.json
    instances_and_feature_vectors = create_feature_vector(json_data["instances"], json_data["featureNames"])
    preprocess_instances_and_feature_vectors(instances_and_feature_vectors)
    instance_and_coord_pairs = project_instances(json_data["transposedProjectionMatrix"], instances_and_feature_vectors)

    show_plot(instance_and_coord_pairs)
    # normalized_feature_vectors = get_normalized_feature_vectors()
    # return {"featureVectors": normalized_feature_vectors.tolist()}
    return "Ok"


if __name__ == '__main__':
    app.run()
