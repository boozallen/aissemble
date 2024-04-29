def before_all(context):
    context.topics = []


def after_scenario(context, tag):
    if hasattr(context, "client"):
        context.client.shutdown()
