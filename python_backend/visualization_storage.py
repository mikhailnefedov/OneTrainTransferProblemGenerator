import itertools

from matplotlib import pyplot as plt


class VisualizationStorage():

    def __init__(self):
        self.y_lim = []
        self.x_lim = []
        self.plots = []

    def add_visualization_by_source(self, instance_coordinate_pairs):
        fig = plt.figure()
        grouped_instances_by_source = itertools.groupby(instance_coordinate_pairs, key= lambda x: x[0]["featureDescription"]["source"])
        for key, group in grouped_instances_by_source:
            group_list = list(group)
            x_coords = [pair[1][0] for pair in group_list]
            y_coords = [pair[1][1] for pair in group_list]
            plt.scatter(x_coords, y_coords)
            
        plt.title("Instance Space By Source")
        plt.xlim(self.x_lim)
        plt.ylim(self.y_lim)
        self.plots.append(fig)

    def get_plots(self):
        return self.plots

    def set_plot_dimensions(self, x_lim, y_lim):
        self.x_lim = x_lim
        self.y_lim = y_lim
